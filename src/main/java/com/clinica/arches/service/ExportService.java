package com.clinica.arches.service;

import com.clinica.arches.model.Paciente;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Sustenta los dos botones globales "Exportar Excel" y "Exportar PDF" del listado de pacientes.
 * Ambos métodos reciben la lista ya filtrada (mismo search/estado que la tabla) para que el
 * archivo exportado coincida con lo que el usuario está viendo.
 */
@Service
public class ExportService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] exportarExcel(List<Paciente> pacientes) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Pacientes");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] columnas = {"Nombre completo", "Cédula", "Fecha nacimiento", "Edad", "Sexo",
                    "Teléfono", "Correo", "Dirección", "Estado"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Paciente p : pacientes) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getNombreCompleto());
                row.createCell(1).setCellValue(p.getCedula() != null ? p.getCedula() : "");
                row.createCell(2).setCellValue(p.getFechaNacimiento() != null ? p.getFechaNacimiento().format(FECHA) : "");
                row.createCell(3).setCellValue(p.getFechaNacimiento() != null
                        ? Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears() : 0);
                row.createCell(4).setCellValue(p.getSexo() != null ? p.getSexo() : "");
                row.createCell(5).setCellValue(p.getTelefono() != null ? p.getTelefono() : "");
                row.createCell(6).setCellValue(p.getCorreo() != null ? p.getCorreo() : "");
                row.createCell(7).setCellValue(p.getDireccion() != null ? p.getDireccion() : "");
                row.createCell(8).setCellValue(p.getEstadoExpediente() != null ? p.getEstadoExpediente() : "");
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generando el Excel de pacientes", e);
        }
    }

    public byte[] exportarPdf(List<Paciente> pacientes) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
            PdfWriter.getInstance(document, out);
            document.open();

            com.lowagie.text.Font tituloFont =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD);
            Paragraph titulo = new Paragraph("Listado de Pacientes", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(7);
            tabla.setWidthPercentage(100);
            com.lowagie.text.Font headerFont =
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD);
            String[] columnas = {"Nombre completo", "Cédula", "Edad", "Sexo", "Teléfono", "Correo", "Estado"};
            for (String col : columnas) {
                PdfPCell cell = new PdfPCell(new Paragraph(col, headerFont));
                cell.setBackgroundColor(new java.awt.Color(230, 230, 230));
                tabla.addCell(cell);
            }

            com.lowagie.text.Font cellFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9);
            for (Paciente p : pacientes) {
                tabla.addCell(new Paragraph(nullSafe(p.getNombreCompleto()), cellFont));
                tabla.addCell(new Paragraph(nullSafe(p.getCedula()), cellFont));
                int edad = p.getFechaNacimiento() != null
                        ? Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears() : 0;
                tabla.addCell(new Paragraph(String.valueOf(edad), cellFont));
                tabla.addCell(new Paragraph(nullSafe(p.getSexo()), cellFont));
                tabla.addCell(new Paragraph(nullSafe(p.getTelefono()), cellFont));
                tabla.addCell(new Paragraph(nullSafe(p.getCorreo()), cellFont));
                tabla.addCell(new Paragraph(nullSafe(p.getEstadoExpediente()), cellFont));
            }

            document.add(tabla);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando el PDF de pacientes", e);
        }
    }

    private String nullSafe(String valor) {
        return valor != null ? valor : "";
    }
}
