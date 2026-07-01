package com.togamma.heladeria.controller.venta;

import com.togamma.heladeria.dto.response.venta.DashboardResponseDTO;
import com.togamma.heladeria.dto.response.venta.DashboardResponseDTO.VentasPorDiaDTO;
import com.togamma.heladeria.dto.response.venta.DashboardResponseDTO.ProductoMasVendidoDTO;
import com.togamma.heladeria.dto.response.venta.DashboardResponseDTO.VentasPorMesaDTO;
import com.togamma.heladeria.model.venta.Venta;
import com.togamma.heladeria.model.venta.EstadoVenta;
import com.togamma.heladeria.repository.venta.VentaRepository;
import com.togamma.heladeria.service.seguridad.ContextService;
import com.togamma.heladeria.service.venta.PdfGenerationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final VentaRepository ventaRepository;
    private final ContextService contextService;
    private final PdfGenerationService pdfGenerationService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> obtenerResumen() {
        Long idSucursal = contextService.getSucursalLogueada().getId();
        
        // Obtener las ventas cobradas de los últimos 30 días
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        List<Venta> ventas = ventaRepository
                .findBySucursalIdAndEstadoAndCreatedAtAfterAndDeletedAtIsNull(idSucursal, EstadoVenta.COBRADA, hace30Dias);

        // 1. Métricas generales
        double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
        long cantidadVentas = ventas.size();
        double ticketPromedio = cantidadVentas > 0 ? totalVentas / cantidadVentas : 0.0;

        // Redondear ticket y total a 2 decimales
        totalVentas = Math.round(totalVentas * 100.0) / 100.0;
        ticketPromedio = Math.round(ticketPromedio * 100.0) / 100.0;

        // 2. Ventas por día
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Double> ventasPorDiaMap = new TreeMap<>(); // TreeMap ordena por fecha
        
        // Inicializar los últimos 30 días con 0.0 para que la gráfica no tenga huecos
        for (int i = 29; i >= 0; i--) {
            String fecha = LocalDateTime.now().minusDays(i).format(formatter);
            ventasPorDiaMap.put(fecha, 0.0);
        }

        // Llenar datos reales
        for (Venta v : ventas) {
            String fecha = v.getCreatedAt().format(formatter);
            ventasPorDiaMap.put(fecha, ventasPorDiaMap.getOrDefault(fecha, 0.0) + v.getTotal());
        }

        List<VentasPorDiaDTO> ventasPorDia = ventasPorDiaMap.entrySet().stream()
                .map(e -> new VentasPorDiaDTO(e.getKey(), Math.round(e.getValue() * 100.0) / 100.0))
                .collect(Collectors.toList());

        // 3. Productos más vendidos
        Map<String, Long> prodMap = new HashMap<>();
        for (Venta v : ventas) {
            v.getDetalles().forEach(d -> {
                if (d.getProducto() != null) {
                    String name = d.getProducto().getNombre();
                    prodMap.put(name, prodMap.getOrDefault(name, 0L) + d.getCantidad());
                }
            });
        }

        List<ProductoMasVendidoDTO> productosMasVendidos = prodMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new ProductoMasVendidoDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // 4. Ventas por mesa
        Map<String, Double> mesaMap = new HashMap<>();
        for (Venta v : ventas) {
            String mesaLabel = v.getMesa() != null ? "Mesa " + v.getMesa().getNumero() : "Para Llevar";
            mesaMap.put(mesaLabel, mesaMap.getOrDefault(mesaLabel, 0.0) + v.getTotal());
        }

        List<VentasPorMesaDTO> ventasPorMesa = mesaMap.entrySet().stream()
                .map(e -> new VentasPorMesaDTO(e.getKey(), Math.round(e.getValue() * 100.0) / 100.0))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new DashboardResponseDTO(
                totalVentas,
                cantidadVentas,
                ticketPromedio,
                ventasPorDia,
                productosMasVendidos,
                ventasPorMesa
        ));
    }

    @org.springframework.web.bind.annotation.PostMapping(value = "/export-pdf", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> exportarPdf(
            @org.springframework.web.bind.annotation.RequestPart(value = "file", required = false) org.springframework.web.multipart.MultipartFile p12File,
            @org.springframework.web.bind.annotation.RequestParam(value = "password", required = false) String password,
            @org.springframework.web.bind.annotation.RequestParam("signerName") String signerName,
            @org.springframework.web.bind.annotation.RequestParam("reason") String reason,
            @org.springframework.web.bind.annotation.RequestParam("location") String location,
            @org.springframework.web.bind.annotation.RequestParam("selfSigned") boolean selfSigned,
            @org.springframework.web.bind.annotation.RequestParam("sign") boolean sign
    ) throws Exception {
        var sucursal = contextService.getSucursalLogueada();
        Long idSucursal = sucursal.getId();
        
        // Obtener las ventas cobradas de los últimos 30 días
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        List<Venta> ventas = ventaRepository
                .findBySucursalIdAndEstadoAndCreatedAtAfterAndDeletedAtIsNull(idSucursal, EstadoVenta.COBRADA, hace30Dias);

        // Agregaciones
        double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
        long cantidadVentas = ventas.size();
        double ticketPromedio = cantidadVentas > 0 ? totalVentas / cantidadVentas : 0.0;

        totalVentas = Math.round(totalVentas * 100.0) / 100.0;
        ticketPromedio = Math.round(ticketPromedio * 100.0) / 100.0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Double> ventasPorDiaMap = new TreeMap<>();
        for (int i = 29; i >= 0; i--) {
            String fecha = LocalDateTime.now().minusDays(i).format(formatter);
            ventasPorDiaMap.put(fecha, 0.0);
        }
        for (Venta v : ventas) {
            String fecha = v.getCreatedAt().format(formatter);
            ventasPorDiaMap.put(fecha, ventasPorDiaMap.getOrDefault(fecha, 0.0) + v.getTotal());
        }
        List<VentasPorDiaDTO> ventasPorDia = ventasPorDiaMap.entrySet().stream()
                .map(e -> new VentasPorDiaDTO(e.getKey(), Math.round(e.getValue() * 100.0) / 100.0))
                .collect(Collectors.toList());

        Map<String, Long> prodMap = new HashMap<>();
        for (Venta v : ventas) {
            v.getDetalles().forEach(d -> {
                if (d.getProducto() != null) {
                    String name = d.getProducto().getNombre();
                    prodMap.put(name, prodMap.getOrDefault(name, 0L) + d.getCantidad());
                }
            });
        }
        List<ProductoMasVendidoDTO> productosMasVendidos = prodMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new ProductoMasVendidoDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        Map<String, Double> mesaMap = new HashMap<>();
        for (Venta v : ventas) {
            String mesaLabel = v.getMesa() != null ? "Mesa " + v.getMesa().getNumero() : "Para Llevar";
            mesaMap.put(mesaLabel, mesaMap.getOrDefault(mesaLabel, 0.0) + v.getTotal());
        }
        List<VentasPorMesaDTO> ventasPorMesa = mesaMap.entrySet().stream()
                .map(e -> new VentasPorMesaDTO(e.getKey(), Math.round(e.getValue() * 100.0) / 100.0))
                .collect(Collectors.toList());

        DashboardResponseDTO data = new DashboardResponseDTO(
                totalVentas,
                cantidadVentas,
                ticketPromedio,
                ventasPorDia,
                productosMasVendidos,
                ventasPorMesa
        );

        byte[] pdfBytes = pdfGenerationService.generateDashboardPdf(data, sucursal.getNombre());

        if (sign) {
            PdfGenerationService.CertificateWithKey keyInfo;
            if (selfSigned) {
                keyInfo = pdfGenerationService.generateSelfSigned(signerName);
            } else {
                if (p12File == null || p12File.isEmpty()) {
                    throw new IllegalArgumentException("Debe proveer un archivo de firma digital.");
                }
                keyInfo = pdfGenerationService.loadFromP12(p12File.getBytes(), password);
            }
            pdfBytes = pdfGenerationService.signPdf(pdfBytes, keyInfo.privateKey(), keyInfo.chain(), reason, location, signerName);
        }

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte-ventas-firmado.pdf\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
