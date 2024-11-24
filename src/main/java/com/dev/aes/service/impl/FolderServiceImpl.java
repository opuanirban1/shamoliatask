package com.dev.aes.service.impl;

import com.dev.aes.config.FileStorageProperties;
import com.dev.aes.constant.FolderActiveStatus;
import com.dev.aes.constant.FolderType;
import com.dev.aes.constant.NotificationType;
import com.dev.aes.entity.*;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.FolderSubPageRequest;
import com.dev.aes.payloads.request.MoveDto;
import com.dev.aes.payloads.request.SubFolderDto;
import com.dev.aes.payloads.response.*;
import com.dev.aes.repository.FileContentFieldRepository;
import com.dev.aes.repository.FileRepository;
import com.dev.aes.repository.FolderRepository;
import com.dev.aes.repository.FolderShareRepository;
import com.dev.aes.service.FolderService;
import com.dev.aes.service.NotificationService;
import com.dev.aes.service.UserService;
import com.dev.aes.util.Pagination;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl implements FolderService {


    private final Logger LOG = LoggerFactory.getLogger(FolderServiceImpl.class);
    public static final String FOLDER_NOT_FOUND = "Folder not found.";
    private final FolderRepository folderRepository;
    private final UserService userService;
    private final FileRepository fileRepository;
    private final NotificationService notificationService;
    private final FolderShareRepository folderShareRepository;
    private final FileContentFieldRepository contentFieldRepository;
    private final String rootPath;

    @Autowired
    Pagination pagination;

    @Autowired
    public FolderServiceImpl(FolderRepository folderRepository, UserService userService,
                             FileRepository fileRepository,
                             NotificationService notificationService,
                             FolderShareRepository folderShareRepository, FileContentFieldRepository contentFieldRepository,
                             FileStorageProperties fileStorageProperties) {
        this.folderRepository = folderRepository;
        this.userService = userService;
        this.fileRepository = fileRepository;
        this.notificationService = notificationService;
        this.folderShareRepository = folderShareRepository;
        this.contentFieldRepository = contentFieldRepository;
        this.rootPath = fileStorageProperties.getUploadDir();
    }

    @Override
    @Transactional
    public Folder createRootFolder(User user) {
        if (folderRepository.existsByUserAndType(user, FolderType.ROOT)) {
            throw new OcrDmsException("Root folder already exists for current user.", HttpStatus.BAD_REQUEST);
        }
        Folder folder = folderRepository.save(convertRootFolderDtoToEntity(user));

        File osRootFolder = new File(rootPath.concat(File.separator).concat(folder.getName()));
        if (!osRootFolder.exists() && folder.getId() != null) {
            osRootFolder.mkdirs();
        }
        return folder;
    }

    private String getFolderPathByTraversing(Folder currentFolder) {
        if (currentFolder.getParent() == null) {
            return currentFolder.getName();
        }
        Folder parentFolder = findFolderById(currentFolder.getParent().getId());
        return getFolderPathByTraversing(parentFolder) + File.separator + currentFolder.getName();
    }

    @Override
    @Transactional
    public Folder createSubFolder(SubFolderDto dto) {
        if (folderRepository.existsByParentIdAndName(dto.getParentId(), dto.getName())) {
            throw new OcrDmsException("Folder name already exists in current directory.", HttpStatus.BAD_REQUEST);
        }
        Folder folder = folderRepository.save(convertSubFolderDtoToEntity(dto));
        String folderPath = folder.getName();
        notificationService.sendMessagesNotification(Notification.builder()
                .folderId(folder.getId())
                .type(NotificationType.FOLDER_CREATED.toString())
                .status(Boolean.TRUE)
                .build());
        folderPath = getFolderPathByTraversing(folder);

        File subFolder = new File(rootPath.concat(File.separator).concat(folderPath));
        if (!subFolder.exists() && folder.getId() != null) {
            subFolder.mkdirs();
        }
        return folder;
    }

    @Override
    public Folder findById(Long folderId) {
        return folderRepository.findById(folderId).orElseThrow(() ->
                new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public FolderResponseDto getRootFolder() {
        User current = userService.getCurrentuser();
        FolderResponseDto folder = getFolderResponseDto(current);
        if (folder != null) return folder;
        else throw new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @Override
    public FolderResponseDto getSubFolder(Long id) {
        return convertFolderResponseDto(findFolderById(id));
    }

    /*@Override
    public DocTypeFilesResponseDto getDocTypeFiles(String docType, List<String> status) {
        Folder root = getRoot();
        if (Objects.isNull(root)) {
            throw new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        System.out.println("Folder root id "+root.getId()+" folder info :"+root.getSubFolders());
        Set<DocFile> byFolder = fileRepository.findByFolder(root);
        System.out.println("byFolder size "+byFolder.size());

        Set<Folder> byParent = folderRepository.findByParent(root);

        System.out.println("bysize "+byFolder.size());


        Set<DocFile> totalDocFiles = new HashSet<>(byFolder);


        for (DocFile docFile: totalDocFiles){


            System.out.println("docfile doctype "+docFile.getDocType() + "file id---"+docFile.getId());
        }

        setTotalFiles(byParent, totalDocFiles);
        Set<DocFile> filterDocFiles = totalDocFiles.stream()
                .filter(file -> file.getDocType().equals(docType)).collect(Collectors.toSet());
        Long unparseDoc = filterDocFiles.stream()
                .filter(file -> file.getStatus().equalsIgnoreCase("CONFIRM")).count();
        Long parseDoc = filterDocFiles.size() - unparseDoc;
        Long pendingConfirmation = filterDocFiles.stream()
                .filter(file -> file.getStatus().equalsIgnoreCase("FAILED")).count();
        Long time = fileRepository.getAverageTime();
        Set<DocFile> unparseDocFiles = filterDocFiles.stream()
                .filter(file -> status.contains(file.getStatus())).collect(Collectors.toSet());
        long estimatedTimeInSec = (Objects.isNull(time)) ? 0 : time * unparseDoc;
        return DocTypeFilesResponseDto.builder()
                .parsedCount(parseDoc)
                .unParseCount(unparseDoc)
                .pendingConfirmationCount(pendingConfirmation)
                .estimatedTimeInSec(estimatedTimeInSec)
                .docFiles(unparseDocFiles)
                .build();
    }*/

   /* @Override
    public DocTypeFilesResponseDto getDocTypeFiles(String docType, List<String> status) {
       try {
           Folder root = getRoot();
           if (Objects.isNull(root)) {
               throw new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND);
           }
           Set<DocFile> byFolder = fileRepository.findByFolder(root);
           Set<Folder> byParent = folderRepository.findByParent(root);
           Set<DocFile> totalDocFiles = new HashSet<>(byFolder);

           //System.out.println("Anirban root" + root.getId() + " byfolder size" + byFolder.size() + " bypatrent size" + byParent.size());
           setTotalFiles(byParent, totalDocFiles);
           Set<DocFile> filterDocFiles = totalDocFiles.stream()
                   .filter(file -> file.getDocType().equalsIgnoreCase(docType)).collect(Collectors.toSet());*/
           /*System.out.println("Filter doc file"+filterDocFiles.size());
           for (DocFile docfile: filterDocFiles){
              System.out.println("doc file ID"+docfile.getId()+" doc type "+ docfile.getDocType()+" doc status "+docfile.getStatus()+" doc file folder id"+docfile.getFolder().getId());
          }*/
          /* Long ocrfin = filterDocFiles.stream()
                   .filter(file -> file.getStatus().equalsIgnoreCase("OCR FINISHED")).count();
           Long confirmed = filterDocFiles.stream()
                   .filter(file -> file.getStatus().equalsIgnoreCase("CONFIRMED")).count();
           Long done = filterDocFiles.stream()
                   .filter(file -> file.getStatus().equalsIgnoreCase("DONE")).count();
           Long parseDoc = ocrfin+confirmed+done;
           Long unparseDoc = filterDocFiles.size()-parseDoc;
           Long pendingConfirmation = filterDocFiles.stream()
                   .filter(file -> file.getStatus().equalsIgnoreCase("FAILED")).count();
           Long time = fileRepository.getAverageTime();
           Set<DocFile> unparseDocFiles = filterDocFiles.stream()
                   .filter(file -> status.contains(file.getStatus())).collect(Collectors.toSet());
           long estimatedTimeInSec = (Objects.isNull(time)) ? 0 : time * unparseDoc;
           return DocTypeFilesResponseDto.builder()
                   .parsedCount(parseDoc)
                   .unParseCount(unparseDoc)
                   .pendingConfirmationCount(pendingConfirmation)
                   .estimatedTimeInSec(estimatedTimeInSec)
                   .docFiles(unparseDocFiles)
                   .build();
       }
       catch (Exception ex){

           ex.printStackTrace();
           return null;
       }
    }*/

    @Override
    public DocTypeFilesResponseDto getDocTypeFiles(String docType, List<String> status) {
        //try {
           /* Folder root = getRoot();
            if (Objects.isNull(root)) {
                throw new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND);
            }*/
            //Set<DocFile> byFolder = fileRepository.findByFolder(root);
            //Set<Folder> byParent = folderRepository.findByParent(root);
            //Set<DocFile> totalDocFiles = new HashSet<>(byFolder);
            User user = userService.getCurrentuser();
            //List<DocTypeLocationFileResponseDto>  res = new ArrayList<DocTypeLocationFileResponseDto>();
            List<Long> getFolderid = fileRepository.getFolderIdByuserid (user.getId());
            Set<DocFile> totalDocFiles2 = fileRepository.getDocFilebyFolderId(getFolderid);
            //System.out.println("Anirban root" + root.getId() + " byfolder size" + byFolder.size() + " bypatrent size" + byParent.size());
            //old
            //setTotalFiles(byParent, totalDocFiles);
            //totalDocFiles2.
            Set<DocFile> filterDocFiles = totalDocFiles2.stream()
                    .filter(file -> file.getDocType().equalsIgnoreCase(docType)).collect(Collectors.toSet());
           /*System.out.println("Filter doc file"+filterDocFiles.size());
           for (DocFile docfile: filterDocFiles){
              System.out.println("doc file ID"+docfile.getId()+" doc type "+ docfile.getDocType()+" doc status "+docfile.getStatus()+" doc file folder id"+docfile.getFolder().getId());
          }*/
            Long ocrfin = filterDocFiles.stream()
                    .filter(file -> file.getStatus().equalsIgnoreCase("OCR FINISHED")).count();
            Long confirmed = filterDocFiles.stream()
                    .filter(file -> file.getStatus().equalsIgnoreCase("CONFIRMED")).count();
            Long done = filterDocFiles.stream()
                    .filter(file -> file.getStatus().equalsIgnoreCase("DONE")).count();
            Long parseDoc = ocrfin+confirmed+done;
            Long unparseDoc = filterDocFiles.size()-parseDoc;
            Long pendingConfirmation = filterDocFiles.stream()
                    .filter(file -> file.getStatus().equalsIgnoreCase("FAILED")).count();
            Long time = fileRepository.getAverageTime();
            Set<DocFile> unparseDocFiles = filterDocFiles.stream()
                    .filter(file -> status.contains(file.getStatus())).collect(Collectors.toSet());
            long estimatedTimeInSec = (Objects.isNull(time)) ? 0 : time * unparseDoc;
            return DocTypeFilesResponseDto.builder()
                    .parsedCount(parseDoc)
                    .unParseCount(unparseDoc)
                    .pendingConfirmationCount(pendingConfirmation)
                    .estimatedTimeInSec(estimatedTimeInSec)
                    .docFiles(unparseDocFiles)
                    .build();
        /*}
        catch (Exception ex){
            //ex.printStackTrace();
            return null;
        }*/
    }


    @Override
    public List<FileResponseDto> searchFileByStatus(Long folderId, String upperCase) {
        Folder folder = findFolderById(folderId);
        Set<DocFile> byFolder = fileRepository.findByFolder(folder);
        Set<Folder> byParent = folderRepository.findByParent(folder);
        Set<DocFile> totalDocFiles = new HashSet<>(byFolder);
        setTotalFiles(byParent, totalDocFiles);
        List<DocFile> docFiles = totalDocFiles.stream().filter(file -> file.getStatus().equalsIgnoreCase(upperCase))
                .toList();
        List<FileResponseDto> responseDtos = docFiles.stream().map(file -> FileResponseDto.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .location(file.getLocation())
                .fileType(file.getDocType())
                .docType(file.getDocType())
                .status(file.getStatus())
                .folderName(file.getFolder().getName())
                .updatedAt(file.getUpdatedAt())
                .createdAt(file.getCreatedAt())
                .build()).collect(Collectors.toList());
        return responseDtos;
    }

    @Override
    public List<FileResponseDto> getDoctypeAndFileStatus(String docType, String status) {
        Folder root = getRoot();
        if (Objects.isNull(root)) {
            throw new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        Set<DocFile> byFolder = fileRepository.findByFolder(root);
        Set<Folder> byParent = folderRepository.findByParent(root);
        Set<DocFile> totalDocFiles = new HashSet<>(byFolder);
        setTotalFiles(byParent, totalDocFiles);
        Set<DocFile> filterDocFiles = totalDocFiles.stream().filter(file -> file.getDocType().equals(docType))
                .collect(Collectors.toSet());
        List<DocFile> docFiles = filterDocFiles.stream().filter(file -> file.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        List<FileResponseDto> responseDtos = docFiles.stream().map(file -> FileResponseDto.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .location(file.getLocation())
                .fileType(file.getDocType())
                .docType(file.getDocType())
                .status(file.getStatus())
                .folderName(file.getFolder().getName())
                .updatedAt(file.getUpdatedAt())
                .createdAt(file.getCreatedAt())
                .build()).collect(Collectors.toList());
        return responseDtos;
    }

    //old
  /*  @Override
    public List<DocTypeLocationFileResponseDto> getDoctypeAndLocationFiles(String docType, List<String> status) {
        Folder root = getRoot();
        if (Objects.isNull(root)) {
            throw new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        Set<DocFile> byFolder = fileRepository.findByFolder(root).stream()
                .filter(file -> file.getDocType().equalsIgnoreCase(docType) && status.contains(file.getStatus()))
                .collect(Collectors.toSet());
        Set<Folder> byParent = folderRepository.findByParent(root);
        String location = root.getName();
        List<DocTypeLocationFileResponseDto> list = new ArrayList<>();
        if (!byFolder.isEmpty()) {
            list.add(DocTypeLocationFileResponseDto.builder().location(location).docFiles(byFolder).build());
        }

        setDocTypeLocationFiles(byParent, list, location, docType, status);
        return list;
    }*/

    //old 2
   /* @Override
    public List<DocTypeLocationFileResponseDto> getDoctypeAndLocationFiles(String docType, List<String> status) {
        User user = userService.getCurrentuser();
        List<DocTypeLocationFileResponseDto>  res = new ArrayList<DocTypeLocationFileResponseDto>();
        List<Long> getFolderid = fileRepository.getFolderIdByuserid (user.getId());
        for (Integer i=0; i <getFolderid.size(); i++){
            Folder  infoFolder = folderRepository.getFolderById(getFolderid.get(i).longValue());
            Folder parentFolder = folderRepository.getFolderById(infoFolder.getParent().getId());
            String location =  parentFolder.getName() +"/"+infoFolder.getName();
            //System.out.println("golder id "+getFolderid.get(i).longValue());
            Set<DocFile> getDocFileByname = fileRepository.getDocFilebyStatusandDoctype(docType.toUpperCase(), status, getFolderid.get(i).longValue() );
            DocTypeLocationFileResponseDto docTypeLocationFileResponseDto = new DocTypeLocationFileResponseDto (
                    location,getDocFileByname
            );
            res.add(docTypeLocationFileResponseDto);
        }// end loop
        return res;
    }*/



    @Override
    public List<DocTypeLocationFileResponseDto> getDoctypeAndLocationFiles(String docType, List<String> status) {
        User user = userService.getCurrentuser();
        List<DocTypeLocationFileResponseDto>  res = new ArrayList<DocTypeLocationFileResponseDto>();
        System.out.println("user id"+user.getId());
        List<Long> getFolderid = fileRepository.getFolderIdByuserid (user.getId());
        for (Integer i=0; i <getFolderid.size(); i++){
            System.out.println("anirban id "+getFolderid.get(i).longValue());
            Folder parentFolder = new Folder();
            Folder  infoFolder = new Folder();
            Long parentid = folderRepository.getFolderParentIdById(getFolderid.get(i).longValue());
            if (parentid ==0){
            }else{
                infoFolder = folderRepository.getFolderById(getFolderid.get(i).longValue());
                parentFolder = folderRepository.getFolderById(infoFolder.getParent().getId());
            }
            String location =  parentFolder.getName() +"/"+infoFolder.getName();
            //System.out.println("golder id "+getFolderid.get(i).longValue());
            Set<DocFile> getDocFileByname = fileRepository.getDocFilebyStatusandDoctype(docType.toUpperCase(), status, getFolderid.get(i).longValue() );
            DocTypeLocationFileResponseDto docTypeLocationFileResponseDto = new DocTypeLocationFileResponseDto (
                    location,getDocFileByname
            );
            res.add(docTypeLocationFileResponseDto);
        }// end loop
        return res;
    }




    @Override
    public List<DocTypeLocationFileResponseDto> getByFolderIdAndStatusWithLocation(Long folderId, List<String> status) {
        Folder folder = findFolderById(folderId);
        Set<DocFile> byFolder = fileRepository.findByFolder(folder).stream()
                .filter(file -> status.contains(file.getStatus()))
                .collect(Collectors.toSet());
        Set<Folder> byParent = folderRepository.findByParent(folder);
        String location = folder.getName();
        List<DocTypeLocationFileResponseDto> list = new ArrayList<>();
        if (!byFolder.isEmpty()) {
            list.add(DocTypeLocationFileResponseDto.builder().location(location).docFiles(byFolder).build());
        }
        setDocTypeLocationFilesByStatus(byParent, list, location, status);
        return list;
    }

    @Override
    public List<ParentFolderResponseDto> getParentFolderList(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        List<ParentFolderResponseDto> responseDtos = new ArrayList<>();
        Folder parent = folder.getParent();
        while (parent != null) {
            responseDtos.add(ParentFolderResponseDto.builder().id(parent.getId()).name(parent.getName()).build());
            parent = parent.getParent();
        }
        return responseDtos.reversed();
    }

    @Override
    @Transactional
    @Modifying
    public String delete(Long folderId) {
        Optional<Folder> folder = folderRepository.findById(folderId);//.orElseThrow(() -> new OcrDmsException(FOLDER_NOT_FOUND));

        if (folder.isEmpty()) {
            throw new OcrDmsException(FOLDER_NOT_FOUND);
        }

        boolean filesExist = fileRepository.existsByFolderId(folder.get().getId());
        if (filesExist) {
            fileRepository.deleteAllByFolder(folder.get());
        }
        List<FolderShare> sharedFolders = folderShareRepository.findAllByFolderId(folder.get().getId());
        if (!sharedFolders.isEmpty()) {
            folderShareRepository.deleteSharedFoldersUsingFolderId(folder.get().getId());
        }

        Set<Folder> subFolders = folderRepository.findByParent(folder.get());
        if (!subFolders.isEmpty()) {
            for (Folder subFolder : subFolders) {

                sharedFolders = folderShareRepository.findAllByFolderId(folder.get().getId());
                if (!sharedFolders.isEmpty()) {
                    folderShareRepository.deleteSharedFoldersUsingFolderId(folder.get().getId());
                }

                if (fileRepository.existsByFolderId(subFolder.getId())) {
                    fileRepository.deleteAllByFolder(subFolder);
                }
                folderRepository.delete(subFolder);
            }
        }


        folderRepository.delete(folder.get());
        String folderPath = rootPath.concat(File.separator).concat(getFolderPathByTraversing(folder.get()));

        File file = new File(folderPath);
        if (file.exists()) {
            if (file.listFiles().length > 0) {
                deleteDirectory(new File(folderPath));
            }
        } else {
            throw new OcrDmsException("File/folder not found in OS.");
        }
        Path path = Paths.get(folderPath);
        File dirPath = new File(path.toString());
        try {
            if (dirPath.exists()){
                Files.delete(path);
           }
        } catch (IOException e) {
            throw new OcrDmsException("File Delete Failed.");
        }

        notificationService.sendMessagesNotification(Notification.builder()
                .folderId(folderId)
                .type(NotificationType.FOLDER_DELETED.toString())
                .status(Boolean.TRUE)
                .build());

        return "Delete Successfully.";
    }

    private void deleteDirectory(File file)
    {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
    }

    @Override
    @Transactional
    public String moveTo(MoveDto moveDto) {
        Folder destinationFolder = folderRepository.findById(moveDto.getDestinationFolderId()).orElseThrow(() -> new OcrDmsException("Target Folder Not Found With Id " + moveDto.getDestinationFolderId()));
        String destinationPath = rootPath.concat(File.separator).concat(getFolderPathByTraversing(destinationFolder));

        if (moveDto.getFolderIds() != null && !moveDto.getFolderIds().isEmpty()) {
            moveDto.getFolderIds().forEach(folderId -> {
//                for (Long folderId: moveDto.getFolderIds()) {
                Folder existFolder = folderRepository.findById(folderId).orElseThrow(() -> new OcrDmsException("Folder Not Found With Id " + folderId));
                Path path = Paths.get(destinationPath);
                Path targetFolderPath = path.resolve(existFolder.getName());
                String sourcePath = getFolderPathByTraversing(existFolder);
                existFolder.setParent(destinationFolder);
                folderRepository.save(existFolder);

                try {
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                    }

                    Files.move(Paths.get(rootPath.concat(File.separator).concat(sourcePath)), targetFolderPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        }

        if (!moveDto.getFileIds().isEmpty()) {
            for (Long fileId: moveDto.getFileIds()) {
//            moveDto.getFileIds().forEach(fileId -> {
                DocFile existDocFile = fileRepository.findById(fileId).orElseThrow(() -> new OcrDmsException("File Not Found With Id " + fileId));
                Folder folder = existDocFile.getFolder();
                existDocFile.setFolder(destinationFolder);
                fileRepository.save(existDocFile);
                String escapedSeparator = File.separator.equals("\\") ? "\\\\" : File.separator;
                String[] tempStrArr = existDocFile.getLocation().split(escapedSeparator);
                Path path = Paths.get(destinationPath.concat(File.separator).concat(tempStrArr[tempStrArr.length-1]));
//                ---------------Folder folder = findFolderById(existDocFile.getFolder().getId());
//                Path sourceFilePath = Paths.get(existDocFile.getLocation());
                Path sourceFilePath = Paths.get(rootPath.concat(File.separator).concat(getFolderPathByTraversing(folder).concat(File.separator).concat(existDocFile.getKeyFileName())));
//                Path targetFolderPath = path.resolve(existFolder.getName());
//                String sourcePath = getFolderPathByTraversing(existFolder);
                try {
                    Files.move(sourceFilePath, path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//            });
            }
        }

        notificationService.sendMessagesNotification(Notification.builder()
                .type(NotificationType.FOLDERS_FILES_MOVED.toString())
                .status(Boolean.TRUE)
                .build());

        return "Moved successfully.";
    }


    @Override
    public List<SearchResponseDto> search(String value, List<Long> userIds) {
        List<Folder> folders = folderRepository.search(value, userIds);
        if (!folders.isEmpty()){
            return folders.stream().map(folder -> SearchResponseDto.builder()
                    .id(folder.getId())
                    .content(folder.getName())
                    .type("FOLDER").build())
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private void checkPermission(Folder folder, Long userId) {
        if (folder.getParent() == null) return;
        while(folder.getUser() == null) {
            if (folderShareRepository.findSharedFolderByUserIdAndFolderId(folder.getId(), userId).isPresent() || folder.getCreatedBy().getId().equals(userId)) {
                return;
            }
            folder = folderRepository.findById(folder.getParent().getId()).get();
        }
        if (folder.getUser().getId().equals(userId)) {
            return;
        }
        if (folder.getCreatedBy().getId().equals(userId)) {
            return;
        }
        throw new OcrDmsException("Access is denied for this folder", HttpStatus.UNAUTHORIZED);
    }

    private void checkPermissionForOrgAmin(Folder folder, Long userId) {

        List<Long> orgAdminFolderPermission = new ArrayList<>();
        List<FolderShare> sharedFolderList = folderShareRepository.findAllByUserId(userId);
        for (FolderShare folderShare: sharedFolderList) {
            orgAdminFolderPermission.add(folderShare.getFolderId());
        }
        if (orgAdminFolderPermission.contains(folder.getId())) {
            return;
        }

        if (folder.getCreatedBy().getId().equals(userId)) {
            return;
        }
        throw new OcrDmsException("Access is denied for this folder", HttpStatus.UNAUTHORIZED);
    }

   /* @Override
    public List<GlobalSearchDto> localGlobalSearch(String searchString, Long folderId,String searchType) {
        User user = userService.getCurrentuser();
        Optional<Folder> folder = Optional.empty();
        Set<String> userRoles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        if (searchType.equalsIgnoreCase("GLOBAL") && !userRoles.contains("ROLE_USER")) {
//            For global search folderId from request can be 0 or null
            folderId = folderRepository.findIdByUsername(user.getUsername());
        } else if (userRoles.contains("ROLE_USER") && searchType.equalsIgnoreCase("GLOBAL")) {
            return globalSearchForUser(searchString, user);
        }
        folder = folderRepository.findById(folderId);
        if (folder.isEmpty()) {
            throw new OcrDmsException("No folder found");
        } else {
            checkPermission(folder.get(), user.getId());
        }


        List<GlobalSearchDto> searchList = new ArrayList<>();
        List<Folder> folderList = new ArrayList<>();
        List<Long> folderIds = new ArrayList<>(List.of());
        List<Long> fileIds = new ArrayList<>(List.of());

        Set<Folder> subFolders = folderRepository.searchByParentFolderId("", folder.get().getId());

        getAllSubFolders(subFolders, folderList);

        folderList.forEach(f -> {
            if (f.getName().toLowerCase().contains(searchString.toLowerCase()))
                searchList.add(GlobalSearchDto.builder().id(f.getId()).name(f.getName()).type("FOLDER").build());
        });

        folderIds.add(folder.get().getId());
        folderList.forEach(el -> folderIds.add(el.getId()));
        List<DocFile> docFiles = fileRepository.searchByFolderIds(searchString, folderIds);
        docFiles.forEach(docFile -> {
            //edited by anirban for fixing global seach folderid issue
            searchList.add(GlobalSearchDto.builder().id(docFile.getId()).name(docFile.getFileName()).type("FILE").folderid(docFile.getFolder().getId().toString()).build());
            fileIds.add(docFile.getId());
        });

        List<DocFile> docFilesWithNoFilter = fileRepository.searchByFolderIds("", folderIds);
        docFilesWithNoFilter.forEach(docFile -> {
            fileIds.add(docFile.getId());
        });
        List<FileContentField> contentFields = contentFieldRepository.searchByFileIds(searchString, fileIds);
        contentFields.forEach(contentField -> searchList.add(GlobalSearchDto.builder().id(contentField.getId()).name(contentField.getOcrValue()).type("CONTENT").build()));
        if (!searchList.isEmpty()) {
            return searchList;
        }

        return List.of();
    }*/

    @Override
    public List<GlobalSearchDto> localGlobalSearch(String searchString, Long folderId,String searchType) {
        User user = userService.getCurrentuser();
        Optional<Folder> folder = Optional.empty();
        Set<String> userRoles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        if (searchType.equalsIgnoreCase("GLOBAL") && !userRoles.contains("ROLE_USER") && !userRoles.contains("ROLE_ORG_ADMIN")) {
//            For global search folderId from request can be 0 or null
            folderId = folderRepository.findIdByUsername(user.getUsername());
        } else if (userRoles.contains("ROLE_USER") && searchType.equalsIgnoreCase("GLOBAL")) {
            return globalSearchForUser(searchString, user);
        } else if (userRoles.contains("ROLE_ORG_ADMIN") && searchType.equalsIgnoreCase("GLOBAL")) {
            return globalSearchForOrgAdmin(searchString, user);
        }
        folder = folderRepository.findById(folderId);
        if (folder.isEmpty()) {
            throw new OcrDmsException("No folder found");
        } else if (userRoles.contains("ROLE_ORG_ADMIN")){
            checkPermissionForOrgAmin(folder.get(), user.getId());
        } else {
            checkPermission(folder.get(), user.getId());
        }


        List<GlobalSearchDto> searchList = new ArrayList<>();
        List<Folder> folderList = new ArrayList<>();
        List<Long> folderIds = new ArrayList<>(List.of());
        List<Long> fileIds = new ArrayList<>(List.of());

        Set<Folder> subFolders = folderRepository.searchByParentFolderId("", folder.get().getId());
        getAllSubFolders(subFolders, folderList);

        folderList.forEach(f -> {
            if (f.getName().toLowerCase().contains(searchString.toLowerCase()))
                searchList.add(GlobalSearchDto.builder().id(f.getId()).name(f.getName()).type("FOLDER").build());
        });

        folderIds.add(folder.get().getId());
        folderList.forEach(el -> folderIds.add(el.getId()));
        List<DocFile> docFiles = fileRepository.searchByFolderIds(searchString, folderIds);
        docFiles.forEach(docFile -> {
            searchList.add(GlobalSearchDto.builder().id(docFile.getId()).name(docFile.getFileName()).type("FILE").build());
            fileIds.add(docFile.getId());
        });

        List<DocFile> docFilesWithNoFilter = fileRepository.searchByFolderIds("", folderIds);
        docFilesWithNoFilter.forEach(docFile -> {
            fileIds.add(docFile.getId());
        });
        List<FileContentField> contentFields = contentFieldRepository.searchByFileIds(searchString, fileIds);
        contentFields.forEach(contentField -> searchList.add(GlobalSearchDto.builder().id(contentField.getFileId()).name(contentField.getOcrValue()).type("CONTENT").build()));
        if (!searchList.isEmpty()) {
            return searchList;
        }

        return List.of();
    }



    private List<GlobalSearchDto> globalSearchForUser(String searchString, User user) {

        List<FolderShare> sharedFolderList = folderShareRepository.findAllByUserId(user.getId());

        if (sharedFolderList.isEmpty()) {
            throw new OcrDmsException("Nothing found");
        }

        List<GlobalSearchDto> searchList = new ArrayList<>();
        List<Folder> folderList = new ArrayList<>();
        List<Long> folderIds = new ArrayList<>(List.of());
        Set<Folder> subFolders = new HashSet<>(Set.of());
        List<Long> fileIds = new ArrayList<>(List.of());

        for (FolderShare folderShare : sharedFolderList) {
            subFolders.addAll(folderRepository.searchByParentFolderId("", folderShare.getFolderId()));
        }

        getAllSubFolders(subFolders, folderList);

        for (FolderShare folderShare : sharedFolderList) {
            folderList.add(folderRepository.findById(folderShare.getFolderId()).get());
        }

        folderList.forEach(f -> {
            if (f.getName().toLowerCase().contains(searchString.toLowerCase()))
                searchList.add(GlobalSearchDto.builder().id(f.getId()).name(f.getName()).type("FOLDER").build());
        });
        sharedFolderList.forEach(el -> folderIds.add(el.getFolderId()));

        folderList.forEach(el -> folderIds.add(el.getId()));
        List<DocFile> docFiles = fileRepository.searchByFolderIds(searchString, folderIds);
        docFiles.forEach(docFile -> {
            searchList.add(GlobalSearchDto.builder().id(docFile.getId()).name(docFile.getFileName()).type("FILE").build());
            fileIds.add(docFile.getId());
        });
        List<DocFile> docFilesWithNoFilter = fileRepository.searchByFolderIds("", folderIds);
        docFilesWithNoFilter.forEach(docFile -> {
            fileIds.add(docFile.getId());
        });
        List<FileContentField> contentFields = contentFieldRepository.searchByFileIds(searchString, fileIds);
        contentFields.forEach(contentField -> searchList.add(GlobalSearchDto.builder().id(contentField.getFileId()).name(contentField.getOcrValue()).type("CONTENT").build()));

        if (!searchList.isEmpty()) {
            return searchList;
        }

        return List.of();
    }


    private List<GlobalSearchDto> globalSearchForOrgAdmin(String searchString, User user) {

        List<FolderShare> sharedFolderList = folderShareRepository.findAllByUserId(user.getId());
        Set<Folder> folders = folderRepository.findByCreatedBy(user);

        if (sharedFolderList.isEmpty() && folders.isEmpty()) {
            throw new OcrDmsException("Nothing found");
        }

        List<GlobalSearchDto> searchList = new ArrayList<>();
        List<Folder> folderList = new ArrayList<>();
        List<Long> folderIds = new ArrayList<>(List.of());
        Set<Folder> subFolders = new HashSet<>(Set.of());
        List<Long> fileIds = new ArrayList<>(List.of());

        for (FolderShare folderShare : sharedFolderList) {
            subFolders.addAll(folderRepository.searchByParentFolderId("", folderShare.getFolderId()));
        }
        for (FolderShare folderShare : sharedFolderList) {
            folders.add(folderRepository.findById(folderShare.getFolderId()).get());
        }

        for (Folder folder : folders) {
            subFolders.addAll(folderRepository.searchByParentFolderId("", folder.getId()));
        }

        getAllSubFolders(subFolders, folderList);
        folderList.addAll(folders);

        folderList.forEach(f -> {
            if (f.getName().toLowerCase().contains(searchString.toLowerCase()))
                searchList.add(GlobalSearchDto.builder().id(f.getId()).name(f.getName()).type("FOLDER").build());
        });
        sharedFolderList.forEach(el -> folderIds.add(el.getFolderId()));

        folderList.forEach(el -> folderIds.add(el.getId()));
        List<DocFile> docFiles = fileRepository.searchByFolderIds(searchString, folderIds);
        docFiles.forEach(docFile -> {
            searchList.add(GlobalSearchDto.builder().id(docFile.getId()).name(docFile.getFileName()).type("FILE").build());
            fileIds.add(docFile.getId());
        });

        List<DocFile> docFilesWithNoFilter = fileRepository.searchByFolderIds("", folderIds);
        docFilesWithNoFilter.forEach(docFile -> {
            fileIds.add(docFile.getId());
        });
        List<FileContentField> contentFields = contentFieldRepository.searchByFileIds(searchString, fileIds);
        contentFields.forEach(contentField -> searchList.add(GlobalSearchDto.builder().id(contentField.getFileId()).name(contentField.getOcrValue()).type("CONTENT").build()));
        if (!searchList.isEmpty()) {
            return searchList;
        }

        return List.of();
    }

    private void getAllSubFolders(Set<Folder> subFolderList, List<Folder> folderList) {
        for (Folder folder : subFolderList) {
            folderList.add(folder);
            System.out.println("folder name: " + folder.getName());
            Set<Folder> subFolders = folderRepository.findByParent(folder);
            if (!subFolders.isEmpty()) {
                getAllSubFolders(subFolders, folderList);
            }
        }
    }

    @Override
    public List<Folder> getFoldersByParentFolderId(Long folderId) {
        Optional<Folder> folder = folderRepository.findById(folderId);
        //Optional<Folder> folder = folderRepository.getAllFolderByIdOrderByIdAsc(folderId);
//        if (folder.isEmpty()) throw new OcrDmsException(FOLDER_NOT_FOUND);
        if (folder.isEmpty()) return List.of();

        Set<Folder> folders = folderRepository.findByParent(folder.get());
        //Set<Folder> folders = folderRepository.findByParentOrderByIdAsc(folder.get().getParent().getId());
        if (!folders.isEmpty()) {
            return folders.stream().toList();
        }
        return List.of();
    }

    private void setDocTypeLocationFilesByStatus(Set<Folder> byParent, List<DocTypeLocationFileResponseDto> list,
                                                 String location, List<String> status) {
        if (Objects.isNull(byParent) || byParent.isEmpty()) {
            return;
        }
        for (Folder folder : byParent) {
            String newLocation = new StringBuilder(location).append("/").append(folder.getName()).toString();
            Set<DocFile> collect = fileRepository.findByFolder(folder).stream()
                    .filter(file -> status.contains(file.getStatus()))
                    .collect(Collectors.toSet());
            if (!collect.isEmpty()) {
                list.add(DocTypeLocationFileResponseDto.builder().location(newLocation)
                        .docFiles(collect).build());
            }

            setDocTypeLocationFiles(folderRepository.findByParent(folder), list, newLocation, status);
        }
    }

    private void setDocTypeLocationFiles(Set<Folder> byParent, List<DocTypeLocationFileResponseDto> list,
                                         String location, List<String> status) {
        if (Objects.isNull(byParent) || byParent.isEmpty()) {
            return;
        }
        for (Folder folder : byParent) {
            String newLocation = new StringBuilder(location).append("/").append(folder.getName()).toString();
            Set<DocFile> collect = fileRepository.findByFolder(folder).stream()
                    .filter(file -> status.contains(file.getStatus()))
                    .collect(Collectors.toSet());
            if (!collect.isEmpty()) {
                list.add(DocTypeLocationFileResponseDto.builder().location(newLocation)
                        .docFiles(collect).build());
            }

            setDocTypeLocationFiles(folderRepository.findByParent(folder), list, newLocation, status);
        }
    }

    private void setDocTypeLocationFiles(Set<Folder> byParent, List<DocTypeLocationFileResponseDto> list,
                                         String location, String docType, List<String> status) {
        if (Objects.isNull(byParent) || byParent.isEmpty()) {
            return;
        }
        for (Folder folder : byParent) {
            String newLocation = new StringBuilder(location).append("/").append(folder.getName()).toString();
            Set<DocFile> collect = fileRepository.findByFolder(folder).stream()
                    .filter(file -> file.getDocType().equalsIgnoreCase(docType) && status.contains(file.getStatus()))
                    .collect(Collectors.toSet());
            if (!collect.isEmpty()) {
                list.add(DocTypeLocationFileResponseDto.builder().location(newLocation)
                        .docFiles(collect).build());
            }

            setDocTypeLocationFiles(folderRepository.findByParent(folder), list, newLocation, docType, status);
        }
    }


    private Folder getRoot() {
        User user = userService.getCurrentuser();
        User current = user;
        while (current != null) {
            List<String> roles = current.getRoles().stream().map(Role::getName).toList();
            if (roles.contains("ROLE_SYSTEM_ADMIN")) {
                Folder folder = folderRepository.findByUserAndType(current, FolderType.ROOT)
                        .orElseThrow(() -> new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND));
                return folder;
            }
            current = current.getParent();
        }
        return null;
    }

    private FolderResponseDto getFolderResponseDto(User user) {
        User current = user;
        while (current != null) {
            List<String> roles = current.getRoles().stream().map(Role::getName).toList();
            if (roles.contains("ROLE_SYSTEM_ADMIN")) {
                Folder folder = folderRepository.findByUserAndType(current, FolderType.ROOT)
                        .orElseThrow(() -> new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND));
                return convertFolderResponseDto(folder);
            }
            current = current.getParent();
        }
        return null;
    }

    private FolderResponseDto convertFolderResponseDto(Folder folder) {
        FolderResponseDto folderResponseDto = new FolderResponseDto();
        folderResponseDto.setId(folder.getId());
        folderResponseDto.setName(folder.getName());
        folderResponseDto.setType(folder.getType());
        folderResponseDto.setActive(folder.getActive());
        Set<Folder> byParent = folderRepository.findByParent(folder);
        folderResponseDto.setSubFolders(byParent.stream().map(folder1 ->
                SubFolderResponseDto.builder()
                        .id(folder1.getId())
                        .name(folder1.getName())
                        .type(folder1.getType())
                        .active(folder1.getActive())
                        .parentId(folder1.getParent().getId())
                        .subFolderCount(folderRepository.countByParentId(folder1.getId()))
                        .shareUsers(folderShareUserNames(folder1.getId()))
                        .build()).collect(Collectors.toSet()));
        Set<DocFile> byFolder = fileRepository.findByFolder(folder);
        Set<DocFile> totalDocFiles = new HashSet<>(byFolder);
        setTotalFiles(byParent, totalDocFiles);
        Long unparseDoc = totalDocFiles.stream().filter(file -> file.getStatus().equalsIgnoreCase("CONFIRM")).count();
        Long parseDoc = totalDocFiles.size() - unparseDoc;
        Long pendingConfirmation = totalDocFiles.stream()
                .filter(file -> file.getStatus().equalsIgnoreCase("FAILED")).count();
        Long time = fileRepository.getAverageTime();
        folderResponseDto.setParsedCount(parseDoc);
        folderResponseDto.setUnParseCount(unparseDoc);
        folderResponseDto.setPendingConfirmationCount(pendingConfirmation);
        long estimatedTimeInSec = (Objects.isNull(time)) ? 0 : time * unparseDoc;
        folderResponseDto.setEstimatedTimeInSec(estimatedTimeInSec);
        /*folderResponseDto.setFiles(byFolder.stream().map(file -> FileResponseDto.builder()
                .id(file.getId())
                .status(file.getStatus())
                .updatedAt(file.getUpdatedAt())
                .location(file.getLocation())
                .createdAt(file.getCreatedAt())
                .fileType(file.getFileType())
                .docType(file.getDocType())
                .fileName(file.getFileName())
                .build()).collect(Collectors.toSet()));*/
        Set<FileResponseDto> byFolderSetFileResDto = byFolder.stream().map(file -> FileResponseDto.builder()
                .id(file.getId())
                .status(file.getStatus())
                .updatedAt(file.getUpdatedAt())
                .location(file.getLocation())
                .createdAt(file.getCreatedAt())
                .fileType(file.getFileType())
                .docType(file.getDocType())
                .fileName(file.getFileName())
                .build()).collect(Collectors.toSet());
        return folderResponseDto;
    }

    private void setTotalFiles(Set<Folder> byParent, Set<DocFile> totalDocFiles) {
        if (Objects.isNull(byParent) || byParent.isEmpty()) {
            return;
        }
        for (Folder folder : byParent) {
            totalDocFiles.addAll(fileRepository.findByFolder(folder));
            setTotalFiles(folderRepository.findByParent(folder), totalDocFiles);
        }
    }

    private Folder convertSubFolderDtoToEntity(SubFolderDto dto) {
        Folder folder = new Folder();
        folder.setParent(findFolderById(dto.getParentId()));
        folder.setName(dto.getName());
        folder.setActive(dto.getActive());
        folder.setType(FolderType.SUB);
        folder.setCreatedBy(userService.getCurrentuser());
        return folder;
    }

    private Folder findFolderById(Long id) {
        if (Objects.isNull(id)) return null;
        return folderRepository.findById(id)
                .orElseThrow(() -> new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    private Folder convertRootFolderDtoToEntity(User user) {
        Folder folder = new Folder();
        folder.setName(user.getUsername());
        folder.setActive(FolderActiveStatus.ACTIVATE);
        folder.setType(FolderType.ROOT);
        folder.setUser(user);
        folder.setCreatedBy(user);
        return folder;
    }


    private List<FolderShareUserResponseDto> folderShareUserNames(Long folderId) {
        List<FolderShareUserResponseDto> list = new ArrayList<>();
        List<FolderShare> folderShares = folderShareRepository.findAllByFolderId(folderId);
        if (!folderShares.isEmpty()) {
            for (FolderShare entity : folderShares) {
                FolderShareUserResponseDto dto = userService.getFolderShareResponseDto(entity.getUserId());
                if (Objects.nonNull(dto)) {
                    list.add(dto);
                }
            }
        }
        return list;
    }

    @Override
    public  FolderResponseDto getRootFolderPagination(FolderSubPageRequest folderSubPageRequest) {
        User current = userService.getCurrentuser();
        LOG.info("user {}", current);
        FolderResponseDto folder = getFolderResponseDtoWithPagination(current,folderSubPageRequest);
        LOG.info("API Response {} ", folder);
        if (folder != null) return folder;
        else throw new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    private FolderResponseDto getFolderResponseDtoWithPagination(User user, FolderSubPageRequest folderSubPageRequest) {
        User current = user;
        while (current != null) {
            List<String> roles = current.getRoles().stream().map(Role::getName).toList();
            if (roles.contains("ROLE_SYSTEM_ADMIN")) {
                LOG.info("Current user id {} and FOlder Type {} ", current.getId(),  FolderType.ROOT );
                Folder folder = folderRepository.findByUserAndType(current, FolderType.ROOT)
                        .orElseThrow(() -> new OcrDmsException(FOLDER_NOT_FOUND, HttpStatus.NOT_FOUND));
                //return convertFolderResponseDto(folder);
                return convertFolderResponseDtoWithPagination(folder,  folderSubPageRequest, folder.getId());
            }
            current = current.getParent();
        }
        return null;
    }

    private FolderResponseDto convertFolderResponseDtoWithPagination(Folder folder, FolderSubPageRequest folderSubPageRequest, Long id) {

        Integer totalrowcount=0,startlimit=0,endlimit=0, totalpage=0, totalrowcountfolder=0, totalpagefolder=0, startlimitfolder=0, endlimitfolder=0;
        Long parseDoc=0L, unparseDoc=0L, pendingConfirmation=0L;
        List<String> alldoctypes = new ArrayList<String>();
        Set<DocFile> byFolder =  new HashSet<>();
        Set<Folder> byParent = new HashSet<>();
        FolderResponseDto folderResponseDto = new FolderResponseDto();
        folderResponseDto.setId(folder.getId());
        folderResponseDto.setName(folder.getName());
        folderResponseDto.setType(folder.getType());
        folderResponseDto.setActive(folder.getActive());
        //Set<Folder> byParent = folderRepository.findByParent(folder);
        //LOG.info("Anirban parent_id {} ", folder.getId() );
        totalrowcountfolder =  folderRepository.searchByParentCount(folder.getId());
        startlimitfolder = pagination.getstartlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
        endlimitfolder = pagination.getendlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
        totalpagefolder = pagination.gettotalnoofpage(totalrowcountfolder, folderSubPageRequest.getLimitperpage());
        byParent = folderRepository.searchByParentWithPagination( startlimitfolder,  endlimitfolder , folder.getId());

        //get All subfolder id
         List<Long> allErrorFolderIds = folderRepository.getSubFolderIdsByFolderId(folder.getId());
         Integer  errorcount= 0;
         errorcount = folderRepository.getErrorCountByFolderIdandErrorStatus(allErrorFolderIds);
         folderResponseDto.setErrorcount(errorcount);

         folderResponseDto.setSubFolders(byParent.stream().map(folder1 ->
                SubFolderResponseDto.builder()
                        .id(folder1.getId())
                        .name(folder1.getName())
                        .type(folder1.getType())
                        .active(folder1.getActive())
                        .parentId(folder1.getParent().getId())
                        .subFolderCount(folderRepository.countByParentId(folder1.getId()))
                        .shareUsers(folderShareUserNames(folder1.getId()))
                        .build()).collect(Collectors.toSet()));

        Set<DocFile> byFolderAllWithOutPagination = fileRepository.findByFolder(folder);
        if(folderSubPageRequest.getStatus().equalsIgnoreCase("ERROR DETECTED")) {

            if (folderSubPageRequest.getDoctypes().isEmpty()) {
                totalrowcount = fileRepository.getFileByDoctypePaginationTotalCountNULLErrorDetect(id, folderSubPageRequest.getStatus().toUpperCase());
                startlimit = pagination.getstartlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
                endlimit = pagination.getendlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
                //LOG.info("NULL total page no  {} per page limit {} ", folderSubPageRequest.getPageno(), folderSubPageRequest.getLimitperpage());
                totalpage = pagination.gettotalnoofpage(totalrowcount, folderSubPageRequest.getLimitperpage());
                //System.out.println("NUll totalrowcount" + totalrowcount + "start limit" + startlimit + "end limit " + endlimit + "total page" + totalpage);
            /*docTypeClassList = repository.getAlldoctypeclassPaginationAll(startlimit, endlimit, doctypeClassSearchPaginatrionReq.getSearchtext(),
                    doctypeClassSearchPaginatrionReq.getStatus(), doctypeClassSearchPaginatrionReq.getStartdate(), doctypeClassSearchPaginatrionReq.getEnddate(), doctypeClassSearchPaginatrionReq.getUpdate_by());
            */
                //System.out.println("Anirban id"+id);
                byFolder = fileRepository. getFileByDoctypePaginationNUllErrorDetect(id, startlimit, endlimit, folderSubPageRequest.getStatus().toUpperCase());
                List<DocFile> byFolderList = new ArrayList<>(byFolder);
                //Collections.sort(List<byFolderList>);
                Set<DocFile> totalFiles = new HashSet<>(byFolderAllWithOutPagination);
                setTotalFiles(byParent, totalFiles);
                Long ocrfin = totalFiles.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("OCR FINISHED")).count();
                Long confirmed = totalFiles.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("CONFIRMED")).count();
                Long done = totalFiles.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("DONE")).count();
                parseDoc = ocrfin + confirmed + done;
                unparseDoc = totalFiles.size() - parseDoc;
                pendingConfirmation = totalFiles.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("FAILED")).count();

            } else {
                totalrowcount = fileRepository.getFileByDoctypePaginationTotalCountErrorDetect(folderSubPageRequest.getDoctypes(), id, folderSubPageRequest.getStatus().toUpperCase());
                startlimit = pagination.getstartlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
                endlimit = pagination.getendlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
                // LOG.info("Data total page no  {} per page limit {} ", folderSubPageRequest.getPageno(), folderSubPageRequest.getLimitperpage());
                totalpage = pagination.gettotalnoofpage(totalrowcount, folderSubPageRequest.getLimitperpage());
                //System.out.println("Data totalrowcount" + totalrowcount + "start limit" + startlimit + "end limit " + endlimit + "total page" + totalpage);
            /*docTypeClassList = repository.getAlldoctypeclassPaginationAll(startlimit, endlimit, doctypeClassSearchPaginatrionReq.getSearchtext(),
                    doctypeClassSearchPaginatrionReq.getStatus(), doctypeClassSearchPaginatrionReq.getStartdate(), doctypeClassSearchPaginatrionReq.getEnddate(), doctypeClassSearchPaginatrionReq.getUpdate_by());
            */
                alldoctypes = folderSubPageRequest.getDoctypes();
                alldoctypes.replaceAll(String::toUpperCase);
                byFolder = fileRepository.getFileByDoctypePaginationErrorDetect(alldoctypes, id, startlimit, endlimit, folderSubPageRequest.getStatus().toUpperCase());
                Set<DocFile> totalFiles = new HashSet<>(byFolderAllWithOutPagination);
                setTotalFiles(byParent, totalFiles);
                Set<DocFile> allMergedFilsByDocType = new HashSet<>();
                for (Integer c = 0; c < folderSubPageRequest.getDoctypes().size(); c++) {
                /*Set<DocFile> filterDocFiles = totalDocFiles.stream()
                        .filter(file -> file.getDocType().equalsIgnoreCase(docType)).collect(Collectors.toSet());*/
                    String doctypesingle = folderSubPageRequest.getDoctypes().get(c).toString();
                    Set<DocFile> docFilesConcat =
                            totalFiles.stream().filter(file -> file.getDocType().equalsIgnoreCase(doctypesingle)).collect(Collectors.toSet());
                    allMergedFilsByDocType.addAll(docFilesConcat);
                }
                Long ocrfin = allMergedFilsByDocType.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("OCR FINISHED")).count();
                Long confirmed = allMergedFilsByDocType.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("CONFIRMED")).count();
                Long done = allMergedFilsByDocType.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("DONE")).count();
                parseDoc = ocrfin + confirmed + done;
                unparseDoc = allMergedFilsByDocType.size() - parseDoc;
                pendingConfirmation = totalFiles.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("FAILED")).count();

            }


        }else {

            if (folderSubPageRequest.getDoctypes().isEmpty()) {
                totalrowcount = fileRepository.getFileByDoctypePaginationTotalCountNULL(id);
                startlimit = pagination.getstartlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
                endlimit = pagination.getendlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
                //LOG.info("NULL total page no  {} per page limit {} ", folderSubPageRequest.getPageno(), folderSubPageRequest.getLimitperpage());
                totalpage = pagination.gettotalnoofpage(totalrowcount, folderSubPageRequest.getLimitperpage());
                //System.out.println("NUll totalrowcount" + totalrowcount + "start limit" + startlimit + "end limit " + endlimit + "total page" + totalpage);
            /*docTypeClassList = repository.getAlldoctypeclassPaginationAll(startlimit, endlimit, doctypeClassSearchPaginatrionReq.getSearchtext(),
                    doctypeClassSearchPaginatrionReq.getStatus(), doctypeClassSearchPaginatrionReq.getStartdate(), doctypeClassSearchPaginatrionReq.getEnddate(), doctypeClassSearchPaginatrionReq.getUpdate_by());
            */
                //System.out.println("Anirban id"+id);
                byFolder = fileRepository.getFileByDoctypePaginationNUll(id, startlimit, endlimit);
                List<DocFile> byFolderList = new ArrayList<>(byFolder);
                //Collections.sort(List<byFolderList>);
                Set<DocFile> totalFiles = new HashSet<>(byFolderAllWithOutPagination);
                setTotalFiles(byParent, totalFiles);
                Long ocrfin = totalFiles.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("OCR FINISHED")).count();
                Long confirmed = totalFiles.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("CONFIRMED")).count();
                Long done = totalFiles.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("DONE")).count();
                parseDoc = ocrfin + confirmed + done;
                unparseDoc = totalFiles.size() - parseDoc;
                pendingConfirmation = totalFiles.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("FAILED")).count();

            } else {
                totalrowcount = fileRepository.getFileByDoctypePaginationTotalCount(folderSubPageRequest.getDoctypes(), id);
                startlimit = pagination.getstartlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
                endlimit = pagination.getendlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
                // LOG.info("Data total page no  {} per page limit {} ", folderSubPageRequest.getPageno(), folderSubPageRequest.getLimitperpage());
                totalpage = pagination.gettotalnoofpage(totalrowcount, folderSubPageRequest.getLimitperpage());
                //System.out.println("Data totalrowcount" + totalrowcount + "start limit" + startlimit + "end limit " + endlimit + "total page" + totalpage);
            /*docTypeClassList = repository.getAlldoctypeclassPaginationAll(startlimit, endlimit, doctypeClassSearchPaginatrionReq.getSearchtext(),
                    doctypeClassSearchPaginatrionReq.getStatus(), doctypeClassSearchPaginatrionReq.getStartdate(), doctypeClassSearchPaginatrionReq.getEnddate(), doctypeClassSearchPaginatrionReq.getUpdate_by());
            */
                alldoctypes = folderSubPageRequest.getDoctypes();
                alldoctypes.replaceAll(String::toUpperCase);
                byFolder = fileRepository.getFileByDoctypePagination(alldoctypes, id, startlimit, endlimit);
                Set<DocFile> totalFiles = new HashSet<>(byFolderAllWithOutPagination);
                setTotalFiles(byParent, totalFiles);
                Set<DocFile> allMergedFilsByDocType = new HashSet<>();
                for (Integer c = 0; c < folderSubPageRequest.getDoctypes().size(); c++) {
                /*Set<DocFile> filterDocFiles = totalDocFiles.stream()
                        .filter(file -> file.getDocType().equalsIgnoreCase(docType)).collect(Collectors.toSet());*/
                    String doctypesingle = folderSubPageRequest.getDoctypes().get(c).toString();
                    Set<DocFile> docFilesConcat =
                            totalFiles.stream().filter(file -> file.getDocType().equalsIgnoreCase(doctypesingle)).collect(Collectors.toSet());
                    allMergedFilsByDocType.addAll(docFilesConcat);
                }
                Long ocrfin = allMergedFilsByDocType.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("OCR FINISHED")).count();
                Long confirmed = allMergedFilsByDocType.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("CONFIRMED")).count();
                Long done = allMergedFilsByDocType.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("DONE")).count();
                parseDoc = ocrfin + confirmed + done;
                unparseDoc = allMergedFilsByDocType.size() - parseDoc;
                pendingConfirmation = totalFiles.stream()
                        .filter(file -> file.getStatus().equalsIgnoreCase("FAILED")).count();

            }
        }
        //Set<DocFile> totalFiles = new HashSet<>(byFolder);
        //Set<DocFile> totalFiles = new HashSet<>(byFolderAllWithOutPagination);
        //setTotalFiles(byParent, totalFiles);
        //old
        /*Long unparseDoc = totalFiles.stream().filter(file -> file.getStatus().equalsIgnoreCase("CONFIRM")).count();
        Long parseDoc = totalFiles.size() - unparseDoc;*/
       /* Long ocrfin = totalFiles.stream()
                .filter(file -> file.getStatus().equalsIgnoreCase("OCR FINISHED")).count();
        Long confirmed = totalFiles.stream()
                .filter(file -> file.getStatus().equalsIgnoreCase("CONFIRMED")).count();
        Long done = totalFiles.stream()
                .filter(file -> file.getStatus().equalsIgnoreCase("DONE")).count();
        Long parseDoc = ocrfin+confirmed+done;
        Long unparseDoc = totalFiles.size()-parseDoc;
        Long pendingConfirmation = totalFiles.stream()
                .filter(file -> file.getStatus().equalsIgnoreCase("FAILED")).count();*/
        Long time = fileRepository.getAverageTime();
        folderResponseDto.setParsedCount(parseDoc);
        folderResponseDto.setUnParseCount(unparseDoc);
        folderResponseDto.setPageno(folderSubPageRequest.getPageno());
        folderResponseDto.setLimitperpage(folderSubPageRequest.getLimitperpage());
        folderResponseDto.setTotalrow( totalrowcount);
        folderResponseDto.setTotalpage(totalpage );
        folderResponseDto.setTotalpageFolder(totalpagefolder);

        if (totalpage >= totalpagefolder){
            folderResponseDto.setTotalpageMain(totalpage);
        }else{
            folderResponseDto.setTotalpageMain(totalpagefolder);
        }

        if(totalrowcount >= totalrowcountfolder){
            folderResponseDto.setTotalrowMain(totalrowcount);
        }else{
            folderResponseDto.setTotalrowMain(totalrowcountfolder);
        }

        folderResponseDto.setTotalrowFolder( totalrowcountfolder);
        folderResponseDto.setPendingConfirmationCount(pendingConfirmation);
        long estimatedTimeInSec = (Objects.isNull(time)) ? 0 : time * unparseDoc;
        folderResponseDto.setEstimatedTimeInSec(estimatedTimeInSec);
         //old
//        /*folderResponseDto.setFiles(byFolder.stream().map(file -> FileResponseDto.builder()
//                .id(file.getId())
//                .status(file.getStatus())
//                .updatedAt(file.getUpdatedAt())
//                .location(file.getLocation())
//                .createdAt(file.getCreatedAt())
//                .fileType(file.getFileType())
//                .docType(file.getDocType())
//                .fileName(file.getFileName())
//                .build()).collect(Collectors.toSet()));*/
        Set<FileResponseDto> byFolderSetFileResDto = byFolder.stream().map(file -> FileResponseDto.builder()
                .id(file.getId())
                .status(file.getStatus())
                .updatedAt(file.getUpdatedAt())
                .location(file.getLocation())
                .createdAt(file.getCreatedAt())
                .fileType(file.getFileType())
                .docType(file.getDocType())
                .fileName(file.getFileName())
                .build()).collect(Collectors.toSet());

        List<FileResponseDto> byFolderSetFileResDtoList = new ArrayList<FileResponseDto>(byFolderSetFileResDto);
        sortArraylistDesc(byFolderSetFileResDtoList);
        folderResponseDto.setFiles(byFolderSetFileResDtoList);

        return folderResponseDto;
    }

    @Override
    public  NextFileIdByDoctypeSubFolderRes  getNextFileIdBtDOctypeRootFolderDo(String doctype){
        NextFileIdByDoctypeSubFolderRes nextFileIdByDoctypeSubFolderRes = new NextFileIdByDoctypeSubFolderRes();
        List<Integer> fileids = new ArrayList<Integer>();
        User current = userService.getCurrentuser();
        FolderResponseDto folder = getFolderResponseDto(current);
        LOG.info("API Response {}", folder.getId());
        fileids = fileRepository.getDBNextFileidByDTypeFolderID(doctype.toUpperCase(), Integer.parseInt(folder.getId().toString()) );
        nextFileIdByDoctypeSubFolderRes.setFileids(fileids);
        LOG.info("API resposne {} ", nextFileIdByDoctypeSubFolderRes);
        return nextFileIdByDoctypeSubFolderRes;
    }

    @Override
    public FolderResponseDto getSubFolderWithPagination(Long id, FolderSubPageRequest folderSubPageRequest){
        return convertFolderResponseDtoWithPagination(findFolderById(id), folderSubPageRequest, id);
    }

    @Override
    public NextFileIdByDoctypeSubFolderRes   getNextFileIdBtDOctypeSubFolderDo(String doctype, Integer id){
        NextFileIdByDoctypeSubFolderRes nextFileIdByDoctypeSubFolderRes = new NextFileIdByDoctypeSubFolderRes();
        List<Integer> fileids = new ArrayList<Integer>();
        fileids = fileRepository.getDBNextFileidByDTypeFolderID(doctype.toUpperCase(), id);
        nextFileIdByDoctypeSubFolderRes.setFileids(fileids);
        LOG.info("API resposne {} ", nextFileIdByDoctypeSubFolderRes);
        return nextFileIdByDoctypeSubFolderRes;
    }


    public  void sortArraylistDesc(List<FileResponseDto> list) {
        list.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));
    }
}
