package com.dev.aes.service.impl;

import com.dev.aes.service.FilePdfValidationService;
import com.itextpdf.text.pdf.PdfReader;
import org.springframework.stereotype.Service;

@Service
public class FilePdfValidationServiceImpl implements FilePdfValidationService {

    @Override
    public Integer getPageNoFromLocaqtion (String pdflocation)  {

        try {
            PdfReader reader = new PdfReader(pdflocation);
            return reader.getNumberOfPages();
        }catch (Exception exception){
            return 0;
        }
    }
}
