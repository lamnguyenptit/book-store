package com.example.login.export;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.example.login.export.AbstractExporter;
import com.example.login.model.dto.CarDto;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class CartPdfExporter extends AbstractExporter {

    public void export(List<CarDto> cartDTOList, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "application/pdf", ".pdf", "giohang_");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

//        BaseFont bfComic = BaseFont.createFont("C:\\Users\\lucnv\\Desktop\\timesnewroman.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        Font font = new Font(bfComic, 12);


        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph paragraph = new Paragraph("List of Order", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(paragraph);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);
        table.setWidths(new float[] {1.2f, 5f, 3.0f, 3.0f});

        writeTableHeader(table);
        writeTableData(table, cartDTOList);

        document.add(table);

        document.close();
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("List of Product", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Order Date", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Total Money ", font));
        table.addCell(cell);

    }

    private void writeTableData(PdfPTable table, List<CarDto> carDtoList) {
        for (CarDto cart : carDtoList) {
            table.addCell(String.valueOf(cart.getId()));
            table.addCell(cart.getProductNamesToExport());
            table.addCell(cart.getCheckoutDate().toString());
            table.addCell(cart.getTotalMoney().toString());

        }
    }
}
