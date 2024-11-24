package com.dev.aes.repository;

import com.dev.aes.entity.FileInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInformationRepository extends JpaRepository<FileInformation, Long>
{


   /* @Query(value = "select a.id as id, a.filename as filename , a.location as location  from dmsocr.fileinformation a where (a.doctype_detect_status =0 or a.doctype_detect_status is null) and (a.active =1) limit ?1" , nativeQuery = true)
    public List<DocTypeDetectFileInfoDto> getDocTypeDetectFileForML (Integer limit);

    @Query(value = "select a.id as id, a.filename as filename , a.location as location  from dmsocr.fileinformation a where (a.ocr_detect_status =0 or a.ocr_detect_status is null) and (a.doctype_detect_status =1) and (a.active =1) limit ?1" , nativeQuery = true)
    public List<ParsingDocFileInfoDto> getParsingDocTypeDetectFileForML (Integer limit);



    @Modifying
    @Transactional
    @Query(value="update dmsocr.fileinformation set doctype_detect_status = '1' , doctype_text =?2 where id = ?1", nativeQuery = true)
    public void  updateFileinfromationbyIdforDoctypedetect (Long id, String doctypetxt);


    @Modifying
    @Transactional
    @Query(value="update dmsocr.fileinformation set ocr_detect_status = '1'  where id = ?1", nativeQuery = true)
    public void  updateFileinfromationbyIdforDocParsing (Long id);*/


    //@Query(value = "SELECT  c.name as name, c.parse_key as parse_key FROM doctype a, doctypeobject b, doctypefield c WHERE a.id= b.doctype_id and a.name= ?1 and b.id= c.doctypeobject_id order by c.sequence" , nativeQuery = true)
//    @Query(value="SELECT c.sequence as sequence, c.name as name, c.parse_key as parse_key , c.type as type" +
//
//            " FROM doctype a, doctypeobject b, doctypefield c WHERE a.id= b.doctype_id and a.name= ?1 and b.id= c.doctypeobject_id order by c.sequence", nativeQuery = true)
//    public List<ParserConfigDto> getParseConfig (String doctype);






}
