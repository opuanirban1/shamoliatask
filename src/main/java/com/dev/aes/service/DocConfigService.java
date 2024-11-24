package com.dev.aes.service;


import com.dev.aes.payloads.request.DocTypeAll;
import com.dev.aes.payloads.response.DocTypeConfigData;

import java.util.List;

public interface DocConfigService {



    public List<DocTypeConfigData> getDocConfigDataByDocType(String doctype);

}
