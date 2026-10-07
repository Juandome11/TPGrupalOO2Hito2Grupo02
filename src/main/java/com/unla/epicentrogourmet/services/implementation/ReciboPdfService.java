package com.unla.epicentrogourmet.services.implementation;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.unla.epicentrogourmet.dtos.ReciboSueldoDTO;
import com.unla.epicentrogourmet.exceptions.NegocioException;
import com.unla.epicentrogourmet.services.IReciboPdfService;

@Service("reciboPdfService")
public class ReciboPdfService implements IReciboPdfService {

	private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
	private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
	private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 11);

	@Override
	public byte[] generar(ReciboSueldoDTO r) {
		NumberFormat moneda = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
		ByteArrayOutputStream salida = new ByteArrayOutputStream();
		Document documento = new Document(PageSize.A4, 50, 50, 50, 50);
		try {
			PdfWriter.getInstance(documento, salida);
			documento.open();

			Paragraph titulo = new Paragraph("Epicentro Gourmet - Recibo de sueldo", TITULO);
			titulo.setSpacingAfter(16);
			documento.add(titulo);

			PdfPTable datos = new PdfPTable(new float[] { 1.2f, 3f });
			datos.setWidthPercentage(100);
			datos.setSpacingAfter(16);
			fila(datos, "Empleado", r.getEmpleado());
			fila(datos, "DNI", r.getDni());
			fila(datos, "Función", r.getTipoEmpleado());
			fila(datos, "Festival", r.getFestival() + " (" + r.getFestivalDesde().format(FECHA) + " al "
					+ r.getFestivalHasta().format(FECHA) + ")");
			fila(datos, "Unidad de venta", r.getUnidad());
			fila(datos, "Duración", r.getDiasFestival() + " días");
			documento.add(datos);

			PdfPTable detalle = new PdfPTable(new float[] { 3f, 1.4f });
			detalle.setWidthPercentage(100);
			encabezado(detalle, "Concepto");
			encabezado(detalle, "Monto");
			filaMonto(detalle, "Sueldo base", moneda.format(r.getSueldoBase()), false);
			filaMonto(detalle, r.getConceptoAdicional(), moneda.format(r.getMontoAdicional()), false);
			filaMonto(detalle, "Porcentaje aplicado según duración del festival",
					r.getPorcentajeAplicado().multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + " %",
					false);
			filaMonto(detalle, "TOTAL A COBRAR", moneda.format(r.getTotalACobrar()), true);
			documento.add(detalle);
		} catch (DocumentException e) {
			throw new NegocioException("No se pudo generar el recibo en PDF.");
		} finally {
			documento.close();
		}
		return salida.toByteArray();
	}

	private void fila(PdfPTable tabla, String etiqueta, String valor) {
		PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, NEGRITA));
		c1.setBorder(PdfPCell.NO_BORDER);
		PdfPCell c2 = new PdfPCell(new Phrase(valor, NORMAL));
		c2.setBorder(PdfPCell.NO_BORDER);
		tabla.addCell(c1);
		tabla.addCell(c2);
	}

	private void encabezado(PdfPTable tabla, String texto) {
		PdfPCell celda = new PdfPCell(new Phrase(texto, NEGRITA));
		celda.setPadding(6);
		tabla.addCell(celda);
	}

	private void filaMonto(PdfPTable tabla, String concepto, String monto, boolean destacado) {
		Font fuente = destacado ? NEGRITA : NORMAL;
		PdfPCell c1 = new PdfPCell(new Phrase(concepto, fuente));
		c1.setPadding(6);
		PdfPCell c2 = new PdfPCell(new Phrase(monto, fuente));
		c2.setPadding(6);
		c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		tabla.addCell(c1);
		tabla.addCell(c2);
	}
}
