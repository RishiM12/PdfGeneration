package com.demo.pdfGen.service;

import com.demo.pdfGen.config.CustomConfig;
import com.demo.pdfGen.dto.CustomerPostDTO;
import com.demo.pdfGen.model.Customer;
import com.demo.pdfGen.repository.PdfGenRepository;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Service
public class PdfGenServiceImpl implements PdfGenService{
    @Autowired
    PdfGenRepository pdfGenRepository;

    @Autowired
    CustomConfig customConfig;

    private final ModelMapper modelMapper;

    public Cell createCell(String content, float borderWidth, int colspan, TextAlignment alignment) {
        Cell cell = new Cell(1, colspan).add(new Paragraph(content));
        cell.setTextAlignment(alignment);
        cell.setBorder(new SolidBorder(borderWidth));
        return cell;
    }

    public static byte[] toByteArray(BufferedImage bi, String format) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        return baos.toByteArray();

    }

    Image createBarcode(String shipmentNumber) throws BarcodeException, OutputException, IOException {
        Barcode barcode;
        BufferedImage bufferedImage;
        barcode = BarcodeFactory.createCode128(shipmentNumber);
        bufferedImage = BarcodeImageHandler.getImage(barcode);
        byte[] bytes;
        bytes = toByteArray(bufferedImage, "jpeg");
        ImageData data = ImageDataFactory.create(bytes);
        return new Image(data);
    }

    @Override
    public Mono<Customer> createCustomerForTest(CustomerPostDTO customerPostDTO) {
        Customer customer = modelMapper.map(customerPostDTO, Customer.class);
        return pdfGenRepository.save(customer);
    }

    @Override
    public Mono<ResponseEntity<InputStreamResource>> createPdf(String number) {
        return pdfGenRepository.findByCustomerNumber(number).flatMap(customer -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(os));
            Document document = new Document(pdfDoc);

            Table table = new Table(2);
            UnitValue full = new UnitValue(UnitValue.PERCENT, 100);
            UnitValue half = new UnitValue(UnitValue.PERCENT, 50);
            table.setWidth(full);

            Cell temp = createCell("Ship To:", 1, 1, TextAlignment.LEFT);
            temp.add(new Paragraph(customer.getCustomerName()));
            temp.add(new Paragraph(customer.getAddress().getAddressLine1()));
            temp.add(new Paragraph(customer.getAddress().getAddressLine2()));
            temp.add(new Paragraph(customer.getAddress().getCity()+" "+customer.getAddress().getStateCode()));
            temp.add(new Paragraph(customer.getAddress().getCountryCode()+" "+customer.getAddress().getPinCode()));
            temp.add(new Paragraph("Mob No.: "+customer.getPhoneNumber().getPhoneNumber()));
            temp.add(new Paragraph("Address type: "+customer.getAddress().getAddressType()));
            table.addCell(temp);

            temp = createCell("", 1, 1, TextAlignment.LEFT);
            temp.add(new Paragraph("PaymentType : Prepaid"));
            temp.add(new Paragraph("Collect : Rs. 0.0"));
            table.addCell(temp.setWidth(half));
            document.add(table);

            Image img = null;
            try {
                img = createBarcode(customer.getShipmentNumber());
            } catch (BarcodeException | OutputException | IOException e) {
                throw new RuntimeException(e);
            }

            Table table1 = new Table(2);
            table1.setWidth(full);
            temp = createCell("Shipment #", 1, 1, TextAlignment.LEFT).setWidth(half);
            img.setHorizontalAlignment(HorizontalAlignment.CENTER);
            temp.add(img);
            temp.add(new Paragraph(customer.getShipmentNumber()).setTextAlignment(TextAlignment.CENTER));
            table1.addCell(temp);

            temp = createCell("Carrier Name: " + customConfig.getCarrierName(), 1, 1, TextAlignment.LEFT).setWidth(half);
            temp.add(new Paragraph("Carrier Service: " + customConfig.getCarrierService()));
            table1.addCell(temp.setWidth(half));
            document.add(table1);

            document.close();

            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            var headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=label.pdf");
            ResponseEntity<InputStreamResource> response = ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(is));
            return Mono.just(response);
        });
    }
}
