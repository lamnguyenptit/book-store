package com.example.login.export;

import com.example.login.model.dto.CarDto;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class CartCsvExporter extends AbstractExporter {
    public void export(List<CarDto> cartDTOList, HttpServletResponse response) throws IOException{

        super.setResponseHeader(response, "text/csv", ".csv", "giohang_");

        Writer writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        writer.write('\uFEFF');

        ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"id", "Danh sách sản phẩm", "Ngày đặt hàng", "Tổng tiền"};
        String[] fieldMapping = {"id", "productNamesToExport", "checkoutDate", "totalMoney"};

        csvWriter.writeHeader(csvHeader);

        for(CarDto cartDTO : cartDTOList){
//            cartDTO.setTotalMoney(cartDTO.getTotalMoney());
//            cartDTO.setCartAndProductDtoList(cartDTO.getCartAndProductDtoList());
            csvWriter.write(cartDTO, fieldMapping);
        }

        csvWriter.close();
    }
}
