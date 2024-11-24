package com.dev.aes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name= "doctypeobject")
@Entity
//@MappedSuperclass
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class DocTypeObject {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="name",  length = 255 )
    private String name;

    @Column(name="active",  length = 255 )
    private String active;


    @Column(name="status",  length = 255 )
    private String status;


    @Column(name="type",  length = 255 )
    private String type;


    @Column(name="recurring_status", length = 255)
    private String  recurring_status;


    @Column(name="sequence")
    private Integer sequence;



    @Column(name="create_at")
    @UpdateTimestamp
    private LocalDateTime create_at;


    @Column(name="update_at")
    @UpdateTimestamp
    private LocalDateTime update_at;


    @Column(name="create_by")
    private Integer create_by;


    @Column(name="update_by")
    private Integer update_by;

    /*@ManyToOne(cascade = CascadeType.ALL)
    private DocType doctype;*/
    @Column(name= "doctype_id")
    private Integer doctype_id;


    /*@ManyToOne(cascade = CascadeType.ALL)
    private DocType doctype;*/




     //@ElementCollection
    //@CollectionTable(name="listdoctypeattibutes")
    //private List<DocTypeAttributesv2> docTypeAttributesList = new ArrayList<DocTypeAttributesv2>();

   /* @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DocTypeField> docTypeFieldsList = new ArrayList<>();*/




}