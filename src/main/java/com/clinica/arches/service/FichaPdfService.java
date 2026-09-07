package com.clinica.arches.service;

import com.clinica.arches.dto.AntecedenteDTO;
import com.clinica.arches.dto.ContactoEmergenciaDTO;
import com.clinica.arches.dto.PacienteDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Sustenta el botón de acción "Imprimir" (ficha individual) de la tabla de pacientes.
 * A diferencia del export general, este PDF trae el detalle completo de UN paciente:
 * datos personales, contactos de emergencia y antecedentes.
 */
@Service
public class FichaPdfService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generarFicha(PacienteDTO paciente) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font subtituloFont = new Font(Font.HELVETICA, 13, Font.BOLD, new java.awt.Color(60, 60, 60));
            Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font valueFont = new Font(Font.HELVETICA, 10);

            document.add(new Paragraph("Ficha del Paciente", tituloFont));
            document.add(new Paragraph(" "));

            // Datos generales
            document.add(new Paragraph("Datos generales", subtituloFont));
            PdfPTable datos = new PdfPTable(2);
            datos.setWidthPercentage(100);
            datos.setSpacingBefore(8);
            agregarFila(datos, "Nombre completo", paciente.getNombreCompleto(), labelFont, valueFont);
            agregarFila(datos, "Cédula", paciente.getCedula(), labelFont, valueFont);
            agregarFila(datos, "Fecha de nacimiento",
                    paciente.getFechaNacimiento() != null ? paciente.getFechaNacimiento().format(FECHA) : "",
                    labelFont, valueFont);
            agregarFila(datos, "Edad", paciente.getEdad() != null ? paciente.getEdad() + " años" : "", labelFont, valueFont);
            agregarFila(datos, "Sexo", paciente.getSexo(), labelFont, valueFont);
            agregarFila(datos, "Dirección", paciente.getDireccion(), labelFont, valueFont);
            agregarFila(datos, "Ocupación", paciente.getOcupacion(), labelFont, valueFont);
            agregarFila(datos, "Teléfono", paciente.getTelefono(), labelFont, valueFont);
            agregarFila(datos, "Correo", paciente.getCorreo(), labelFont, valueFont);
            agregarFila(datos, "Estado del expediente", paciente.getEstadoExpediente(), labelFont, valueFont);
            document.add(datos);

            // Contactos de emergencia
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Contactos de emergencia", subtituloFont));
            if (paciente.getContactos() == null || paciente.getContactos().isEmpty()) {
                document.add(new Paragraph("Sin contactos registrados.", valueFont));
            } else {
                PdfPTable tablaContactos = new PdfPTable(3);
                tablaContactos.setWidthPercentage(100);
                tablaContactos.setSpacingBefore(8);
                agregarEncabezado(tablaContactos, labelFont, "Nombre", "Teléfono", "Parentesco");
                for (ContactoEmergenciaDTO c : paciente.getContactos()) {
                    tablaContactos.addCell(new Paragraph(nullSafe(c.getNombreContacto()), valueFont));
                    tablaContactos.addCell(new Paragraph(nullSafe(c.getTelefono()), valueFont));
                    tablaContactos.addCell(new Paragraph(nullSafe(c.getParentesco()), valueFont));
                }
                document.add(tablaContactos);
            }

            // Antecedentes
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Antecedentes", subtituloFont));
            if (paciente.getAntecedentes() == null || paciente.getAntecedentes().isEmpty()) {
                document.add(new Paragraph("Sin antecedentes registrados.", valueFont));
            } else {
                PdfPTable tablaAntecedentes = new PdfPTable(3);
                tablaAntecedentes.setWidthPercentage(100);
                tablaAntecedentes.setSpacingBefore(8);
                agregarEncabezado(tablaAntecedentes, labelFont, "Tipo", "Descripción", "Fecha de registro");
                for (AntecedenteDTO a : paciente.getAntecedentes()) {
                    tablaAntecedentes.addCell(new Paragraph(nullSafe(a.getTipoAntecedente()), valueFont));
                    tablaAntecedentes.addCell(new Paragraph(nullSafe(a.getDescripcion()), valueFont));
                    tablaAntecedentes.addCell(new Paragraph(
                            a.getFechaRegistro() != null ? a.getFechaRegistro().format(FECHA) : "", valueFont));
                }
                document.add(tablaAntecedentes);
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando la ficha del paciente", e);
        }
    }

    private void agregarFila(PdfPTable tabla, String label, String valor, Font labelFont, Font valueFont) {
        PdfPCell celdaLabel = new PdfPCell(new Paragraph(label, labelFont));
        celdaLabel.setBorder(Rectangle.NO_BORDER);
        celdaLabel.setPaddingBottom(4);
        PdfPCell celdaValor = new PdfPCell(new Paragraph(nullSafe(valor), valueFont));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setPaddingBottom(4);
        tabla.addCell(celdaLabel);
        tabla.addCell(celdaValor);
    }

    private void agregarEncabezado(PdfPTable tabla, Font labelFont, String... columnas) {
        for (String col : columnas) {
            PdfPCell cell = new PdfPCell(new Paragraph(col, labelFont));
            cell.setBackgroundColor(new java.awt.Color(230, 230, 230));
            tabla.addCell(cell);
        }
    }

    private String nullSafe(String valor) {
        return valor != null ? valor : "";
    }
}
