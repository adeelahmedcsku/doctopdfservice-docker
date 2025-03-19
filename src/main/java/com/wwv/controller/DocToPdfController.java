package com.wwv.controller;

import org.docx4j.Docx4J;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;

@RestController
public class DocToPdfController {

    private static final Logger logger = LoggerFactory.getLogger(DocToPdfController.class);

    // Directory where the converted PDFs will be saved.
    @Value("${pdf.output.dir}")
    private String pdfOutputDir; // Injected from application.properties

    @PostMapping("/convert")
    public ResponseEntity<byte[]> convertToPdf(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "url", required = false) String url) {
        try {
            InputStream docxInputStream;

            // Check if a file is uploaded
            if (file != null && !file.isEmpty()) {
                docxInputStream = file.getInputStream();
            }
            // Check if a URL is provided
            else if (url != null && !url.isEmpty()) {
                // Download the file from the URL
               // this is for http url
               try {
                   URL docxUrl = new URL(url);
                   docxInputStream = docxUrl.openStream();
                   // end of http url
               }
               catch (Exception e)
               {
                   // Load the DOCX file into a WordprocessingMLPackage
                   File docxFile = new File(url);
                   if (!docxFile.exists()) {
                       return ResponseEntity.badRequest().body("Hardcoded DOCX file not found.".getBytes());
                   }
                   docxInputStream = new FileInputStream(docxFile);
               }


            } else {
                return ResponseEntity.badRequest().body("Either a file or a URL must be provided.".getBytes());
            }

            // Load the DOCX file into a WordprocessingMLPackage
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxInputStream);

            // Convert the DOCX to PDF using XSL FO
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            Docx4J.toPDF(wordMLPackage, pdfOutputStream);

            byte[] pdfBytes = pdfOutputStream.toByteArray();

            // Save the PDF file to disk (optional)
            File outputDir = new File(pdfOutputDir);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            String pdfFileName = "converted_" + System.currentTimeMillis() + ".pdf";
            File pdfFile = new File(outputDir, pdfFileName);
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(pdfBytes);
            }
            System.out.println("PDF saved at: " + pdfFile.getAbsolutePath());

            // Prepare HTTP headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", pdfFileName);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error converting DOCX file: " + e.getMessage()).getBytes());
        }
    }


    @PostMapping("/convert-hardcoded")
    public ResponseEntity<byte[]> convertHardcodedDocxToPdf() {
        try {
            // Hardcoded path to the DOCX file
            String hardcodedDocxFilePath = "C:/Users/adeel/Downloads/1742210089886_OrderConfirmation_5001.docx"; // Replace with the actual path

            // Load the DOCX file into a WordprocessingMLPackage
            File docxFile = new File(hardcodedDocxFilePath);
            if (!docxFile.exists()) {
                return ResponseEntity.badRequest().body("Hardcoded DOCX file not found.".getBytes());
            }

            InputStream docxInputStream = new FileInputStream(docxFile);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxInputStream);

            // Convert the DOCX to PDF using XSL FO
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            Docx4J.toPDF(wordMLPackage, pdfOutputStream);

            byte[] pdfBytes = pdfOutputStream.toByteArray();

            // Save the PDF file to disk (optional)
            File outputDir = new File(pdfOutputDir);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            String pdfFileName = "converted_" + System.currentTimeMillis() + ".pdf";
            File pdfFile = new File(outputDir, pdfFileName);
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(pdfBytes);
            }
            System.out.println("PDF saved at: " + pdfFile.getAbsolutePath());

            // Prepare HTTP headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", pdfFileName);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error converting hardcoded DOCX file: " + e.getMessage()).getBytes());
        }
    }

}