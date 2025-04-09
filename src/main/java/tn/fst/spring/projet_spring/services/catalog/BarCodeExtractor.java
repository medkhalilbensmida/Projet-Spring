package tn.fst.spring.projet_spring.services.catalog;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Service
public class BarCodeExtractor {

    public String extraireCodeBarre(MultipartFile fichierImage) throws Exception {
        BufferedImage image = ImageIO.read(fichierImage.getInputStream());

        if (image == null) {
            throw new IllegalArgumentException("Fichier non valide ou format d'image non supporté.");
        }

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();

        } catch (NotFoundException e) {
            throw new Exception("Aucun code-barres détecté dans l'image.");
        }
    }

}