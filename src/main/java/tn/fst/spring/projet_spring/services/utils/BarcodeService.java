package tn.fst.spring.projet_spring.services.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import tn.fst.spring.projet_spring.dto.products.BarcodeExtractionResponse;

@Service
public class BarcodeService {

    public BarcodeExtractionResponse extractBarcodeFromImage(MultipartFile file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fichier image invalide ou corrompu.");
            }

            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.POSSIBLE_FORMATS, List.of(
                    BarcodeFormat.EAN_13,
                    BarcodeFormat.UPC_A,
                    BarcodeFormat.CODE_128,
                    BarcodeFormat.CODE_39,
                    BarcodeFormat.QR_CODE,
                    BarcodeFormat.PDF_417,
                    BarcodeFormat.DATA_MATRIX
            ));

            Result result = new MultiFormatReader().decode(bitmap, hints);
            String barcode = result.getText();
            boolean isTunisian = barcode.startsWith("619");

            return new BarcodeExtractionResponse(barcode, isTunisian);

        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucun code-barres détecté dans l'image.");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors du traitement de l'image.");
        }
    }

}
