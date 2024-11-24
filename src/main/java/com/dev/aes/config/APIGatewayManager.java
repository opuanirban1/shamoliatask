package com.dev.aes.config;

import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.exception.ParserAnnotatorException;
import com.dev.aes.payloads.request.*;
import com.dev.aes.payloads.response.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class APIGatewayManager {

    @Value("${ml.filetype.detect.api}")
    private String filetypeApiUrl;

    @Value("${parser.annotator.login.uri}")
    private String annotatorLoginUri;
    @Value("${parser.annotator.login.email}")
    private String annotatorLoginEmail;
    @Value("${parser.annotator.login.password}")
    private String annotatorLoginPassword;

    private String filetypeapiurl;

    @Value("${parser.ocrrun.api}")
    private String ocrRunApiUrl;
    @Value("${parser.annotator.pages.uri}")
    private String annotatorPagesUri;

    @Value("${parser.get.all.doctype.url}")
    private String parserGetAllDocTypeUrl;

    @Value("${parser.get.config.field.url}")
    private String parserGetConfigFieldUrl;

    @Value("${parser.annotator.add.to.dictionary.api}")
    private String parserAnnotatorAddToDictionaryApi;

    @Value("${parser.annotator.classifier.api}")
    private String classifierApi;

    @Value("${ml.doctypecreate.endpoint}")
    private String mldoctypecreateapi;

    @Value("${ml.translation.endpoint}")
    private String mltranslationapi;

    @Value("${an.mainclass.url}")
    private String anmainclassurl;

    @Value("${an.subclass.url}")
    private String ansubclassurl;

    @Value("${an.docfields.url}")
    private String andocfieldsurl;

    @Value("${an.lastdoctypebyid.ur}")
    private String anlastdoctypebyidur;

    @Value("${an.reqdoctype.url}")
    private String anreqdoctypeurl;

    @Value("${an.reqdoctypefield.url}")
    private String anreqdoctypefieldurl;

    @Value("${an.uploaddocuserwise.url}")
    private String anuploaddocuserwiseurl;

    @Value("${an.getdoctypeidbyid.url}")
    private String angetdoctypeidbyidurl;

    @Value("${an.getalldoctypefieldall.url}")
    private String angetalldoctypefieldallurl;

    @Value("${an.getdocsubpagebyid.url}")
    private String angetdocsubpagebyidurl;


    @Value("${an.getapprovedcotypebyuser.url}")
    private String angetapprovedcotypebyuserurl;

    @Value("${an.finalsubmitdoctypeid.url}")
    private String anfinalsubmitdoctypeidurl;

    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(APIGatewayManager.class);
    public APIGatewayManager(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    public String getDocTypeDetectResponse(String location, String filename) throws Exception {
        String apiResponse = "";
        DocTypeClassifierResponse mlDetectDocTypeResponse = new DocTypeClassifierResponse();
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        String mainlocation = location;
        logger.info("Location {}", mainlocation);
        builder.part("file", new FileSystemResource(location));
        apiResponse = webClient.post()
                .uri(filetypeApiUrl)
                .header("token", "dgTJhasd@^&532132")
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3500))
                .onTerminateDetach()
                .onErrorMap(throwable -> {
                    log.error("error: {}", throwable);
                    if (throwable instanceof OcrDmsException) {
                        throw new OcrDmsException(throwable.getMessage());
                    } else if (throwable instanceof TimeoutException) {
                        throw new OcrDmsException("Request processing failed due to system delay. Please try after sometime.");
                    } else {
                        throw new OcrDmsException("Request processing failed. Please try again");
                    }
                })
                .block();

        logger.info("**** Anirban API Response {}", apiResponse);
        if (apiResponse.isBlank() || apiResponse.contains("error") || apiResponse.contains("Parsing failed.")){
            log.error("DocType Detection Failed For file location: {}", location);
            throw new OcrDmsException("DocType Detection Failed.");
        }
        mlDetectDocTypeResponse = new Gson().fromJson(apiResponse, DocTypeClassifierResponse.class);
        if (Objects.isNull(mlDetectDocTypeResponse.getDoc_type())) {
            log.error("DocType Detection Failed For file location: {}", location);
            throw new OcrDmsException("DocType Detection Failed.");
        }
        //return mlDetectDocTypeResponse.getDoc_type().toUpperCase();
        // upper case remove by  for ML matching 9/15/24
        return mlDetectDocTypeResponse.getDoc_type();
    }
   /* public List<Long> getAllPages(Long docTypeId){
        List<Long> pageIdList = new ArrayList<>();
        String response =
                webClient.get().uri(annotatorPagesUri + docTypeId)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .retrieve()
                        .bodyToMono(String.class).block();
        List<DocTypeResponseDto> list = new Gson().fromJson(response, new TypeToken<List<DocTypeResponseDto>>() {
        }.getType());
        list.forEach(docTypeResponseDto -> {
            pageIdList.add(docTypeResponseDto.getId());
        });
        return pageIdList;
    }*/

    public Map<String, String> getRunOcrResponse(String location, String docType) {
        String apiResponse = "";

        logger.info("Location: {} Doc Type: {} URL : {}", location, docType, ocrRunApiUrl);
        apiResponse = webClient.post()
                .uri(ocrRunApiUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", new FileSystemResource(location))
                        .with("docType", docType))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3500))
                .onTerminateDetach()
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .doOnSuccess(response -> logger.info("response {}", response))
                .block();
        logger.info("Response: {}", apiResponse);
        if (apiResponse.isBlank() || apiResponse.contains("Parsing failed.") || apiResponse.equals("{\"field\":\"Error\"}")){
            return null;
        }
        HashMap<String, String> stringStringHashMap =
                new Gson().<HashMap<String, String>>fromJson(apiResponse, new TypeToken<HashMap<String, String>>() {
        }.getType());
        return stringStringHashMap;
    }
    private String logInToAnnotator(){
        Map<String, String> map = new HashMap<>();
        map.put("email", annotatorLoginEmail);
        map.put("password", annotatorLoginPassword);
        String response =
                webClient.post().uri(annotatorLoginUri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(map).retrieve()
                        .bodyToMono(String.class).block();
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        return jsonObject.get("token").getAsString();
    }

    public List<DocTypeResponseDto> getAllDocType() {
        String apiResponse = "";
        //String accessToken = logInToAnnotator();
        apiResponse = webClient.get()
                .uri(parserGetAllDocTypeUrl)
                .header("Authorization", "Bearer "  + logInToAnnotator())
                .retrieve()
                .bodyToMono(String.class)
                .onTerminateDetach()
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .doOnSuccess(response -> logger.info("response {}", response))
                .block();
        logger.info("***Response: {}", apiResponse);
        List<DocTypeResponseDto> list = new Gson().fromJson(apiResponse, new TypeToken<List<DocTypeResponseDto>>() {
        }.getType());
        return list;
    }

    public List<DocTypeFieldResponseDto> getAllDocTypeFieldByDocType(Long docTypeId) {
        String apiResponse = "";
        apiResponse = webClient.get()
                .uri(parserGetConfigFieldUrl + docTypeId)
                .header("Authorization", "Bearer " + logInToAnnotator())
                .retrieve()
                .bodyToMono(String.class)
                .onTerminateDetach()
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .doOnSuccess(response -> logger.info("response {}", response))
                .block();
        logger.info("Response: {}", apiResponse);
        List<DocTypeFieldResponseDto> list = new Gson().fromJson(apiResponse, new TypeToken<List<DocTypeFieldResponseDto>>() {
        }.getType());
        return list;
    }

    public String postRequestToParserAnnotatorAddToDictionary(AddToDictionaryParserAnnotatorDto dto){
        String apiResponse = webClient.post()
                .uri(parserAnnotatorAddToDictionaryApi)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), AddToDictionaryParserAnnotatorDto.class)
                .retrieve()
                .bodyToMono(String.class)
                .onTerminateDetach()
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .doOnSuccess(response -> logger.info("response {}", response))
                .block();
        return apiResponse;
    }

    public String postRequestToParserAnnotatorClassifier(ParserAnnotatorFileInfoDto dto){
        String apiResponse = webClient.post()
                .uri(classifierApi)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), ParserAnnotatorFileInfoDto.class)
                .retrieve()
                .bodyToMono(String.class)
                .onTerminateDetach()
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .doOnSuccess(response -> logger.info("response {}", response))
                .block();
        return apiResponse;
    }


    public String  getAllPages(){
        System.out.println("URL"+annotatorPagesUri);
        String response =
                webClient.get().uri(annotatorPagesUri+"/api/v1/docType/all")
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        return response;
    }
    public String  getDocTypeSubclassBydoctypeid(Integer doctypeid){
        String response =
                webClient.get().uri(annotatorPagesUri+"/api/v1/docType/getDoctypePage/"+doctypeid.toString())
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        return response;
    }
    public String  getDocTypeFieldBydoctypeid(Integer doctypeid){
        String response =
                webClient.get().uri(annotatorPagesUri+"/api/v1/docType/docTypeField/"+doctypeid.toString())
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        return response;
    }


    public ResponseEntity<Object> getDoctypeFileByDoctypeId(Integer id) throws IOException {
        //DocType docType = new DocType();
        // docType = docTypeRepository.getDocTypeById(id);
        // System.out.println("id"+id+"file type"+docType.getFileType());
        byte[] response = null;
        try {
            response =
                    webClient.get().uri(annotatorPagesUri + "/api/v1/docType/file/" + id.toString())
                            .header("Authorization", "Bearer " + logInToAnnotator())
                            //.accept(MediaType.IMAGE_JPEG)
                            .retrieve()
                            .bodyToMono(byte[].class)
                            .timeout(Duration.ofSeconds(300))
                            /*.onTerminateDetach()
                            .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                            .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))*/
                            .block();

       /* HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();*/

            InputStream is = new BufferedInputStream(new ByteArrayInputStream(response));
            String mimeType = URLConnection.guessContentTypeFromStream(is);

            //System.out.println("********* media type of docuemnt"+mimeType);

            if (mimeType == null){
               mimeType =  getDoctypeFileTypeById (id.toString());
            }

            //System.out.println("********* Final media type of docuemnt"+mimeType);

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf(mimeType))
                    .body(response);
        }catch (Exception ex){

            return ResponseEntity.status(HttpStatus.OK)
                    //.contentType(MediaType.valueOf(mimeType))
                    .body(response);

        }

    }
    public String  getDocTypeFieldByid(Integer id){
        logger.info("API end point /api/v1/docType/docTypeFieldById/{}", id);
        String response =
                webClient.get().uri(annotatorPagesUri+"/api/v1/docType/docTypeFieldById/"+id)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        return response;
    }

    public String getDoctypeIdByName (String doctypename){
        logger.info("API end point /api/v1/docType/getDoctypeIdByName/{}", doctypename);
        String  response =
                webClient.get().uri(annotatorPagesUri+"/api/v1/docType/getDoctypeIdByName/"+doctypename)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        return response;

    }

    public String getDoctypeFileTypeById (String doctypeid){
        logger.info("API end point /api/v1/docType/getDoctypeAllInfoById/{}", doctypeid);
        String  response =
                webClient.get().uri(annotatorPagesUri+"/api/v1/docType/getDoctypeAllInfoById/"+doctypeid)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        return response;

    }

    public List<DocTypeAllResponse>  getAllPagesBYDoctype(){
        logger.info("URL {}",annotatorPagesUri);
        String response =
                webClient.get().uri(annotatorPagesUri+"/api/v1/docType/all")
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        logger.info("***Response: {}", response);
        List<DocTypeAllResponse> list = new Gson().fromJson(response, new TypeToken<List<DocTypeAllResponse>>() {
        }.getType());
        return list;
    }

    public String  getTranslationInfo(String text, String type) throws Exception{
        System.out.println("URL"+mltranslationapi);
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("to_lang", type);
        builder.part("text", text);
        String response =webClient.post()
                .uri(mltranslationapi)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3500))
                .onTerminateDetach()
                .onErrorMap(throwable -> {
                    log.error("error: {}", throwable);
                    if (throwable instanceof ParserAnnotatorException) {
                        throw new ParserAnnotatorException(throwable.getMessage());
                    } else if (throwable instanceof TimeoutException) {
                        throw new ParserAnnotatorException("Request processing failed due to system delay. Please try after sometime.");
                    } else {
                        throw new ParserAnnotatorException("Request processing failed. Please try again");
                    }
                })
                .block();
        MLTransAPIResponse mlTransAPIResponse = new MLTransAPIResponse();
        mlTransAPIResponse  =new Gson().fromJson(response, MLTransAPIResponse.class);
        System.out.println("Res"+mlTransAPIResponse.getMessage());

        return mlTransAPIResponse.getMessage();
    }

    /*

    @PostMapping("/create/mainclass")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<DocTypeReqByUser> createDocTypeMainClass(@Valid @RequestBody DocTypeCreateMainClassDto dto) {
        return new ResponseEntity<>(docTypeService.createDocTypeMainClasseqByUser(dto), HttpStatus.CREATED);
    }


    @PostMapping("/create/subclass")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<DocTypeReqByUser> createDocTypeSubClass(@Valid @RequestBody DocTypeCreateSubClassDto dto) {
        return new ResponseEntity<>(docTypeService.createDocTypeSubClasseqByUser(dto), HttpStatus.CREATED);
    }


    @PostMapping("/docTypeField/{docTypeId}/{docTypePageId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<DocTypeFieldReqByUser> createDocTypeField(@Valid @RequestBody DocTypeFieldCreateDto dto,
                                                                    @PathVariable Long docTypeId, @PathVariable Long docTypePageId) throws Exception {
        return new ResponseEntity<>(docTypeService.createDocTypeFieldReqByUser(dto, docTypeId,docTypePageId), HttpStatus.CREATED);
    }

     */
    public AnnoViewDocTypeCreationRes  doAnnotatorViewMainClassCreation (DocTypeCreateMainClassDto docTypeCreateMainClassDto){
        logger.info("API URL {} and input {}", anmainclassurl, docTypeCreateMainClassDto);
        String response = webClient
                        .post().uri(anmainclassurl)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .body(BodyInserters.fromValue(docTypeCreateMainClassDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        logger.info("API Response: {}", response);
        /*List<DocTypeAllResponse> list = new Gson().fromJson(response, new TypeToken<List<DocTypeAllResponse>>() {
        }.getType());*/
        AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
        return annoViewDocTypeCreationRes;
    }

    public AnnoViewDocTypeCreationRes  doAnnotatorViewSubClassCreation (DocTypeCreateSubClassDto docTypeCreateSubClassDto){
        logger.info("API URL {} and input {}", ansubclassurl, docTypeCreateSubClassDto);
        String response = webClient
                .post().uri(ansubclassurl)
                .header("Authorization", "Bearer " + logInToAnnotator())
                .body(BodyInserters.fromValue(docTypeCreateSubClassDto))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(300))
                .onTerminateDetach()
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                .block();
        logger.info("API Response: {}", response);
        /*List<DocTypeAllResponse> list = new Gson().fromJson(response, new TypeToken<List<DocTypeAllResponse>>() {
        }.getType());*/
        AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
        return annoViewDocTypeCreationRes;
    }

    public AnnoViewDocTypeFieldCreationRes  doAnnoViewDocTypeFieldCreationRes (DocTypeFieldCreateDto docTypeFieldCreateDto, Long doctypeid, Long doctypepageid){
        logger.info("API URL {} and API request {} ", andocfieldsurl, docTypeFieldCreateDto);
        String response = webClient
                .post().uri(andocfieldsurl+"/"+doctypeid+"/"+doctypepageid)
                .header("Authorization", "Bearer " + logInToAnnotator())
                .body(BodyInserters.fromValue(docTypeFieldCreateDto))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(300))
                .onTerminateDetach()
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                .block();
        logger.info("API Response: {}", response);
        /*List<DocTypeAllResponse> list = new Gson().fromJson(response, new TypeToken<List<DocTypeAllResponse>>() {
        }.getType());*/
        AnnoViewDocTypeFieldCreationRes annoViewDocTypeFieldCreationRes = new Gson().fromJson(response,  AnnoViewDocTypeFieldCreationRes.class);
        return annoViewDocTypeFieldCreationRes;
    }

    //anlastdoctypebyidur
    public AnnoViewDocTypeCreationRes  getLastDoctypeById (Long doctypeid){
        logger.info("API end point /api/v1/docType/getLastDoctypeById/{}", doctypeid);
        String  response =
                webClient.get().uri(anlastdoctypebyidur+"/"+doctypeid)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        logger.info("API Response: {}", response);
        AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
        return annoViewDocTypeCreationRes;
    }

    public List<AnnoViewDocTypeCreationRes>  getRequsetedDoctypeByUserId (RequstedDoctypeByUserReq requstedDoctypeByUserReq){
        logger.info("API end point /api/v1/docType/requestedDocTypeByUserId and input {} url {}", requstedDoctypeByUserReq, anreqdoctypeurl);
        String  response =
                webClient.post().uri(anreqdoctypeurl)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .body(BodyInserters.fromValue(requstedDoctypeByUserReq))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        logger.info("****** API Response: {}", response);
        //AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
        List<AnnoViewDocTypeCreationRes> annoViewDocTypeCreationRes = new Gson().fromJson(response, new TypeToken<List<AnnoViewDocTypeCreationRes>>() {
        }.getType());
        return annoViewDocTypeCreationRes;
    }

    public AnnoViewDocTypeCreationRes getDoctypeById(Long id){
        logger.info("API end point /api/v1/docType/requestedDocTypeByUserId and input {}", id);
        System.out.println("URL"+angetdoctypeidbyidurl+"/"+id);
        String  response =
                webClient.get().uri( angetdoctypeidbyidurl+"/"+id)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        //.body(BodyInserters.fromValue(requstedDoctypeByUserReq))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        logger.info("****** API Response: {}", response);
        //AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
        AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
        return annoViewDocTypeCreationRes;
    }

    //anreqdoctypefieldurl
    public List<AnnotatorViewDoctypeFieldByDocIDRes>  getRequsetedDoctypeFieldByDocId (Long doctypoeid){
        logger.info("API end point /api/v1/docType/requestedDocTypeByUserId and input {]", doctypoeid);
        String  response =
                webClient.get().uri(anreqdoctypefieldurl+"/"+doctypoeid)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        //.body(BodyInserters.fromValue(anreqdoctypefieldurl))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        logger.info("API Response: {}", response);
        //AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
        List<AnnotatorViewDoctypeFieldByDocIDRes> annotatorViewDoctypeFieldByDocIDRes = new Gson().fromJson(response, new TypeToken<List<AnnotatorViewDoctypeFieldByDocIDRes>>() {
        }.getType());
        return annotatorViewDoctypeFieldByDocIDRes;
    }

     public AnnoViewDocTypeCreationRes  uploadDocByUser(Long doctypeid, Long userid, String location, String filename)
     {
         String apiResponse = "";
         DocTypeClassifierResponse mlDetectDocTypeResponse = new DocTypeClassifierResponse();
         MultipartBodyBuilder builder = new MultipartBodyBuilder();
         String mainlocation = location;
         logger.info("Location {}", mainlocation);
         builder.part("file", new FileSystemResource(location));

         logger.info("File location {}",location);

         apiResponse = webClient.post()
                 .uri(anuploaddocuserwiseurl+"/"+doctypeid+"/"+userid)
                 .header("Authorization", "Bearer " + logInToAnnotator())
                 .body(BodyInserters.fromMultipartData(builder.build()))
                 .retrieve()
                 .bodyToMono(String.class)
                 .timeout(Duration.ofSeconds(5500))
                 .onTerminateDetach()
                 .onErrorMap(throwable -> {
                     log.error("error: {}", throwable);
                     if (throwable instanceof OcrDmsException) {
                         throw new OcrDmsException(throwable.getMessage());
                     } else if (throwable instanceof TimeoutException) {
                         throw new OcrDmsException("Request processing failed due to system delay. Please try after sometime.");
                     } else {
                         throw new OcrDmsException("Request processing failed for multipage pdf. Please try again");
                     }
                 })
                 .block();

         logger.info("API Response: {}",  apiResponse);
         //AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
         AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson( apiResponse, AnnoViewDocTypeCreationRes.class);
         return annoViewDocTypeCreationRes;

     }

     public List<AllDocTypeFieldTypeResponse> getAllDoctypeFieldTypes(){

         logger.info("API end point /api/v1/docType/angetalldoctypefieldallurl");
         String  response =
                 webClient.get().uri( angetalldoctypefieldallurl)
                         .header("Authorization", "Bearer " + logInToAnnotator())
                         //.body(BodyInserters.fromValue(anreqdoctypefieldurl))
                         .accept(MediaType.APPLICATION_JSON)
                         .retrieve()
                         .bodyToMono(String.class)
                         .timeout(Duration.ofSeconds(300))
                         .onTerminateDetach()
                         .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                         .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                         .block();
         logger.info("API Response: {}", response);
         //AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
         List<AllDocTypeFieldTypeResponse> annotatorViewDoctypeFieldByDocIDRes = new Gson().fromJson(response, new TypeToken<List<AllDocTypeFieldTypeResponse>>() {
         }.getType());
         return annotatorViewDoctypeFieldByDocIDRes;

     }

     public List<AnnoViewDocTypeCreationRes> getDoctypePage(Long doctypeid){

         logger.info("API end point  input {} url {}", angetdocsubpagebyidurl, doctypeid);
         String  response =
                 webClient.get().uri(angetdocsubpagebyidurl+"/"+doctypeid)
                         .header("Authorization", "Bearer " + logInToAnnotator())
                         .accept(MediaType.APPLICATION_JSON)
                         .retrieve()
                         .bodyToMono(String.class)
                         .timeout(Duration.ofSeconds(300))
                         .onTerminateDetach()
                         .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                         .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                         .block();
         logger.info("****** API Response: {}", response);
         //AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
         List<AnnoViewDocTypeCreationRes> annoViewDocTypeCreationRes = new Gson().fromJson(response, new TypeToken<List<AnnoViewDocTypeCreationRes>>() {
         }.getType());
         return annoViewDocTypeCreationRes;


     }


     //angetapprovedcotypebyuserurl
    public List<AnnoViewDocTypeCreationRes> getApprovedDoctypeByUserId (Long userid){

        logger.info("API end point  input {} url {}",angetapprovedcotypebyuserurl, userid);
        String  response =
                webClient.post().uri(angetapprovedcotypebyuserurl+"/"+userid)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        logger.info("****** API Response: {}", response);
        //AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
        List<AnnoViewDocTypeCreationRes> annoViewDocTypeCreationRes = new Gson().fromJson(response, new TypeToken<List<AnnoViewDocTypeCreationRes>>() {
        }.getType());
        return annoViewDocTypeCreationRes;


    }

    public AnnoViewDocTypeCreationRes finalSubmitDoctypeIdAmarDoc (Long doctypeid){

        logger.info("API end point  input {} url {}", anfinalsubmitdoctypeidurl, doctypeid);
        String  response =
                webClient.post().uri(anfinalsubmitdoctypeidurl+"/"+doctypeid)
                        .header("Authorization", "Bearer " + logInToAnnotator())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(300))
                        .onTerminateDetach()
                        .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                        .doOnSuccess(apiresponse -> logger.info("response {}", apiresponse))
                        .block();
        logger.info("****** API Response: {}", response);
        //AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response, AnnoViewDocTypeCreationRes.class);
        AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new Gson().fromJson(response,AnnoViewDocTypeCreationRes.class );
        return annoViewDocTypeCreationRes;


    }

}