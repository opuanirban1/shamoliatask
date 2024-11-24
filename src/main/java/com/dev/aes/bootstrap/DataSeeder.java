package com.dev.aes.bootstrap;

import com.dev.aes.config.APIGatewayManager;
import com.dev.aes.entity.Role;
import com.dev.aes.payloads.response.DocTypeResponseDto;
import com.dev.aes.repository.RoleRepository;
import com.dev.aes.service.DocTypeFieldService;
import com.dev.aes.service.DocTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder /*implements ApplicationListener<ContextRefreshedEvent>*/{
   /* private final RoleRepository roleRepository;
    private final APIGatewayManager apiGatewayManager;
    private final DocTypeService docTypeService;
    private final DocTypeFieldService docTypeFieldService;



    public DataSeeder(RoleRepository roleRepository,
                      APIGatewayManager apiGatewayManager,
                      DocTypeService docTypeService,
                      DocTypeFieldService docTypeFieldService) {
        this.roleRepository = roleRepository;
        this.apiGatewayManager = apiGatewayManager;
        this.docTypeService = docTypeService;
        this.docTypeFieldService = docTypeFieldService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadData();
    }

    private void loadData() {
        if (!(roleRepository.count() > 0)) {
            roleRepository.saveAll(Arrays.asList(Role.builder().name("ROLE_SYSTEM_ADMIN").build(),
                    Role.builder().name("ROLE_ORG_ADMIN").build(),
                    Role.builder().name("ROLE_USER").build()));
        }
        List<DocTypeResponseDto> allDocType = apiGatewayManager.getAllDocType();
        docTypeService.saveAllDocType(allDocType);
        docTypeFieldService.saveDocTypesFields(docTypeService.getAllDocType());
    }*/
}