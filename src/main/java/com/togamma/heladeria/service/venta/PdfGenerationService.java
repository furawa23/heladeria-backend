package com.togamma.heladeria.service.venta;

import com.togamma.heladeria.dto.response.venta.DashboardResponseDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfWriter;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PdfGenerationService {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static record CertificateWithKey(PrivateKey privateKey, Certificate[] chain) {}

    /**
     * Carga las llaves de un archivo PKCS12 (.p12/.pfx) provisto por el usuario.
     */
    public CertificateWithKey loadFromP12(byte[] p12Bytes, String password) throws Exception {
        KeyStore keystore = KeyStore.getInstance("PKCS12", "BC");
        keystore.load(new ByteArrayInputStream(p12Bytes), password.toCharArray());

        String alias = "";
        Enumeration<String> aliases = keystore.aliases();
        while (aliases.hasMoreElements()) {
            alias = aliases.nextElement();
            if (keystore.isKeyEntry(alias)) {
                break;
            }
        }

        PrivateKey key = (PrivateKey) keystore.getKey(alias, password.toCharArray());
        Certificate[] chain = keystore.getCertificateChain(alias);

        if (key == null || chain == null) {
            throw new IllegalArgumentException("No se encontró una clave privada o cadena de certificados válida.");
        }

        return new CertificateWithKey(key, chain);
    }

    /**
     * Genera un certificado auto-firmado temporal en memoria.
     */
    public CertificateWithKey generateSelfSigned(String signerName) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        X500Name dnName = new X500Name("CN=" + signerName + ", O=Sistema Heladeria, C=PE");
        BigInteger certSerialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Date startDate = new Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000); // Ayer
        Date endDate = new Date(System.currentTimeMillis() + 365L * 24L * 60L * 60L * 1000L); // 1 año

        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA")
                .setProvider("BC")
                .build(keyPair.getPrivate());

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                dnName, certSerialNumber, startDate, endDate, dnName, keyPair.getPublic()
        );

        X509CertificateHolder certHolder = certBuilder.build(contentSigner);
        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certHolder);

        return new CertificateWithKey(keyPair.getPrivate(), new Certificate[]{cert});
    }

    /**
     * Genera el PDF base con el reporte estructurado del Dashboard.
     */
    public byte[] generateDashboardPdf(DashboardResponseDTO data, String sucursalNombre) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 54, 72); // Mayor margen inferior para la firma
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // 1. Título
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(20, 40, 80));
        Paragraph title = new Paragraph("REPORTE COMERCIAL DE VENTAS", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);

        // 2. Información de Metadatos
        Font metaFont = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(100, 100, 100));
        Paragraph meta = new Paragraph(
                "Sucursal: " + sucursalNombre + 
                "\nRango del Reporte: Últimos 30 días" +
                "\nFecha de Emisión: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                metaFont
        );
        meta.setSpacingAfter(20);
        document.add(meta);

        // 3. Tabla KPI (Cards)
        PdfPTable kpiTable = new PdfPTable(3);
        kpiTable.setWidthPercentage(100);
        kpiTable.setSpacingAfter(20);
        
        kpiTable.addCell(createKpiCell("INGRESOS TOTALES", "S/ " + String.format("%.2f", data.totalVentas())));
        kpiTable.addCell(createKpiCell("VENTAS REALIZADAS", data.cantidadVentas() + " transacciones"));
        kpiTable.addCell(createKpiCell("TICKET PROMEDIO", "S/ " + String.format("%.2f", data.ticketPromedio())));
        document.add(kpiTable);

        // 4. Grid de Productos y Mesas
        PdfPTable gridTable = new PdfPTable(2);
        gridTable.setWidthPercentage(100);
        gridTable.setSpacingAfter(25);
        
        float[] columnWidths = {1f, 1f};
        gridTable.setWidths(columnWidths);

        // Celda Izquierda: Productos más vendidos
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPaddingRight(10);
        Paragraph prodTitle = new Paragraph("Top 5 Productos Estrella", new Font(Font.HELVETICA, 12, Font.BOLD, new Color(40, 40, 40)));
        prodTitle.setSpacingAfter(10);
        leftCell.addElement(prodTitle);

        PdfPTable prodTable = new PdfPTable(2);
        prodTable.setWidthPercentage(100);
        prodTable.addCell(createHeaderCell("Producto"));
        prodTable.addCell(createHeaderCell("Cantidad"));
        for (var p : data.productosMasVendidos()) {
            prodTable.addCell(createNormalCell(p.producto()));
            prodTable.addCell(createNormalCell(p.cantidad() + " und."));
        }
        leftCell.addElement(prodTable);
        gridTable.addCell(leftCell);

        // Celda Derecha: Ventas por mesa
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPaddingLeft(10);
        Paragraph mesaTitle = new Paragraph("Ventas por Mesa / Canal", new Font(Font.HELVETICA, 12, Font.BOLD, new Color(40, 40, 40)));
        mesaTitle.setSpacingAfter(10);
        rightCell.addElement(mesaTitle);

        PdfPTable mTable = new PdfPTable(2);
        mTable.setWidthPercentage(100);
        mTable.addCell(createHeaderCell("Mesa / Canal"));
        mTable.addCell(createHeaderCell("Monto"));
        for (var m : data.ventasPorMesa()) {
            mTable.addCell(createNormalCell(m.mesa()));
            mTable.addCell(createNormalCell("S/ " + String.format("%.2f", m.total())));
        }
        rightCell.addElement(mTable);
        gridTable.addCell(rightCell);

        document.add(gridTable);

        // 5. Tabla de Ventas Diarias (Solo con ventas > 0)
        Paragraph dTitle = new Paragraph("Evolución de Ingresos Diarios", new Font(Font.HELVETICA, 12, Font.BOLD, new Color(40, 40, 40)));
        dTitle.setSpacingAfter(10);
        document.add(dTitle);

        PdfPTable dayTable = new PdfPTable(2);
        dayTable.setWidthPercentage(100);
        dayTable.addCell(createHeaderCell("Fecha"));
        dayTable.addCell(createHeaderCell("Monto Vendido"));
        for (var d : data.ventasPorDia()) {
            if (d.total() > 0) {
                dayTable.addCell(createNormalCell(d.fecha()));
                dayTable.addCell(createNormalCell("S/ " + String.format("%.2f", d.total())));
            }
        }
        document.add(dayTable);

        document.close();
        return baos.toByteArray();
    }

    /**
     * Aplica la firma criptográfica PKCS#7 al PDF de entrada.
     */
    public byte[] signPdf(byte[] pdfBytes, PrivateKey privateKey, Certificate[] chain, String reason, String location, String signerName) throws Exception {
        PdfReader reader = new PdfReader(pdfBytes);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Firma compatible con el último estándar de firmas digitales
        PdfStamper stamper = PdfStamper.createSignature(reader, baos, '\0');

        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);

        // Posición del recuadro visual de firma: Esquina inferior izquierda (margen de 36px)
        appearance.setVisibleSignature(new Rectangle(36, 36, 280, 110), reader.getNumberOfPages(), "SignatureDigital");

        // Texto visual similar a Adobe Acrobat
        String fechaFirma = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss -05'00'").format(new Date());
        String infoFirma = "Firmado Digitalmente por:\n" + signerName +
                           "\nMotivo: " + reason +
                           "\nUbicación: " + location +
                           "\nFecha: " + fechaFirma;
        appearance.setLayer2Text(infoFirma);
        appearance.setAcro6Layers(false);

        // Definir diccionario criptográfico
        PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setContact(appearance.getContact());
        dic.setDate(new com.lowagie.text.pdf.PdfDate(new GregorianCalendar()));
        appearance.setCryptoDictionary(dic);

        // Reservar espacio para la firma criptográfica en bytes
        int contentEstimated = 8000;
        HashMap<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.CONTENTS, contentEstimated * 2 + 2);
        appearance.preClose(exc);

        // Calcular el hash SHA-256 del contenido firmado
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        InputStream data = appearance.getRangeStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = data.read(buf)) > 0) {
            md.update(buf, 0, n);
        }
        byte[] hash = md.digest();

        // Firmar los atributos usando la clave privada y BouncyCastle
        Calendar cal = new GregorianCalendar();
        PdfPKCS7 sgn = new PdfPKCS7(privateKey, chain, null, "SHA-256", "BC", false);
        byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, cal, null);
        sgn.update(sh, 0, sh.length);
        byte[] extSignature = sgn.getEncodedPKCS7(hash, cal, null, null);

        // Insertar la firma en los bytes reservados
        byte[] paddedSig = new byte[contentEstimated];
        System.arraycopy(extSignature, 0, paddedSig, 0, extSignature.length);

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
        appearance.close(dic2);

        return baos.toByteArray();
    }

    private PdfPCell createKpiCell(String title, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(12);
        cell.setBackgroundColor(new Color(240, 244, 248));
        cell.setBorderColor(new Color(210, 220, 230));

        Paragraph tPar = new Paragraph(title, new Font(Font.HELVETICA, 8, Font.BOLD, new Color(120, 130, 140)));
        Paragraph vPar = new Paragraph(value, new Font(Font.HELVETICA, 13, Font.BOLD, new Color(20, 40, 80)));
        cell.addElement(tPar);
        cell.addElement(vPar);
        return cell;
    }

    private PdfPCell createHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
        cell.setBackgroundColor(new Color(30, 60, 100));
        cell.setPadding(6);
        cell.setBorderColor(new Color(200, 200, 200));
        return cell;
    }

    private PdfPCell createNormalCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(50, 50, 50))));
        cell.setPadding(6);
        cell.setBorderColor(new Color(220, 220, 220));
        return cell;
    }
}
