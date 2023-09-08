package com.dreamtown.danarresidence.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.dreamtown.danarresidence.entity.ContactPerson;
import com.dreamtown.danarresidence.entity.LogAktivitas;
import com.dreamtown.danarresidence.entity.Photo;
import com.dreamtown.danarresidence.entity.Property;
import com.dreamtown.danarresidence.entity.PropertyDetails;
import com.dreamtown.danarresidence.entity.Video;
import com.dreamtown.danarresidence.entity.Website;
import com.dreamtown.danarresidence.repository.LogAktivitasRepository;
import com.dreamtown.danarresidence.repository.MWilayahRepository;
import com.dreamtown.danarresidence.repository.PropertyDetailsRepository;
import com.dreamtown.danarresidence.repository.PropertyRepository;
import com.dreamtown.danarresidence.repository.WebsiteRepository;
import com.dreamtown.danarresidence.service.WebsiteService;
import com.dreamtown.danarresidence.utils.CetakFormulirPemesananRumah;
import com.dreamtown.danarresidence.utils.UUIDGenerator;
import com.dreamtown.danarresidence.utils.Utils;
import com.dreamtown.danarresidence.viewmodel.ViewModelCetakFormulirPemesananRumah;
import static org.springframework.http.MediaType.*;
@Controller
@RequestMapping(value = "/property")
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyDetailsRepository propertyDetailsRepository;

    @Autowired
    private Environment env;

    @Autowired
    private MWilayahRepository mWilayahRepository;

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private WebsiteRepository websiteRepository;

    @Autowired
    private LogAktivitasRepository logAktivitasRepository;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private Utils utils;

    @RequestMapping(method = RequestMethod.GET)
    public String property(Model model) {
        Website web = websiteRepository.findAll().get(0);
        model.addAttribute("listWilayah", mWilayahRepository.findAll());
        model.addAttribute("website", web);
        model.addAttribute("websiteName", websiteService.websiteName());
        if (activeProfile.equalsIgnoreCase("production")) {
            logAktivitasRepository.save(new LogAktivitas(null, "Property", "/property"));
        }
        model.addAttribute("logAktivitas", utils.logAktivitas());
        return "property";
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<Map> postProperty(@RequestPart Property property, @RequestPart MultipartFile file)
            throws IllegalStateException, IOException {
        Map response = new HashMap<>();
        if (file.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        String[] splitFileName = file.getOriginalFilename().split("\\.");
        String extension = splitFileName[splitFileName.length - 1];
        String fileName = UUIDGenerator.generateType4UUID().toString() + "." + extension;
        File fileTemp = new File(env.getProperty("storage.images") + fileName);
        file.transferTo(fileTemp);
        property.setPropertyBanner(fileName);
        propertyRepository.save(property);
        response.put("message", "Property Berhasil di tambahkan");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/post/{idProperty}", method = RequestMethod.POST)
    public ResponseEntity<Map> postDetailsProperty(@PathVariable Integer idProperty,
            @RequestPart PropertyDetails propertyDetails, @RequestPart(required = false) List<MultipartFile> images,
            @RequestPart(required = false) List<MultipartFile> videos)
            throws IllegalStateException, IOException {
        Map response = new HashMap<>();
        propertyDetails.setIdProperty(idProperty);
        if (images != null && !images.isEmpty()) {
            List<Photo> listPhoto = new ArrayList<>();
            for (MultipartFile file : images) {
                String[] splitFileName = file.getOriginalFilename().split("\\.");
                String extension = splitFileName[splitFileName.length - 1];
                String fileName = UUIDGenerator.generateType4UUID().toString() + "." +
                        extension;
                File fileTemp = new File(env.getProperty("storage.images") + fileName);
                try {
                    file.transferTo(fileTemp);
                    Photo photo = new Photo(null, fileName,
                            propertyDetails.getIdDetailsProperty());
                    listPhoto.add(photo);
                } catch (IOException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
            propertyDetails.setListPhoto(listPhoto);
        }
        if (videos != null && !videos.isEmpty()) {
            List<Video> listVideo = new ArrayList<>();
            for (MultipartFile file : videos) {
                String[] splitFileName = file.getOriginalFilename().split("\\.");
                String extension = splitFileName[splitFileName.length - 1];
                String fileName = UUIDGenerator.generateType4UUID().toString() + "." +
                        extension;
                File fileTemp = new File(env.getProperty("storage.videos") + fileName);
                try {
                    file.transferTo(fileTemp);
                    Video photo = new Video(null, fileName,
                            propertyDetails.getIdDetailsProperty());
                    listVideo.add(photo);
                } catch (IOException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
            propertyDetails.setListVideo(listVideo);
        }
        propertyDetailsRepository.save(propertyDetails);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/update/{idProperty}", method = RequestMethod.POST)
    public ResponseEntity<Map> updateDetailsProperty(@PathVariable Integer idProperty,
            @RequestPart PropertyDetails propertyDetails, @RequestPart List<MultipartFile> images,
            @RequestPart List<MultipartFile> videos, @RequestPart List<ContactPerson> contactPersons)
            throws IllegalStateException, IOException {
        Map response = new HashMap<>();
        propertyDetailsRepository.save(propertyDetails);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/del", method = RequestMethod.DELETE)
    public ResponseEntity<Map> delPropertyById(@RequestParam Integer id) {
        Map response = new HashMap<>();
        if (!propertyRepository.existsById(id)) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        propertyRepository.deleteById(id);
        response.put("message", "Berhasil menghapus data");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map> findByIdAndTipeProperty(@PathVariable Integer id, @RequestParam String tipeProperty) {
        Map response = new HashMap<>();
        response.put("property", propertyRepository.findById(id).get());
        response.put("propertyDetails",
                propertyDetailsRepository.findOneByIdPropertyAndTipeProperty(id, tipeProperty));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/getTipeById", method = RequestMethod.GET)
    public ResponseEntity<Map> getTipeById(@RequestParam Integer id) {
        Map response = new HashMap<>();
        response.put("listTipeProperty", propertyDetailsRepository.findDistinctTipePropertyByIdProperty(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Autowired
    private CetakFormulirPemesananRumah cetakFormulirPemesananRumah;

    @RequestMapping(value = "/cetakFormulirPemesanan/{idProperty}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<Map> cetakFormulirPemesanan(@PathVariable Integer idProperty,
            @RequestBody ViewModelCetakFormulirPemesananRumah vmCetakRumah) throws IOException {
        if (vmCetakRumah.getNamaProperty().equalsIgnoreCase("")) {
            Map response = new HashMap();
            response.put("message", "File Not Found");
            ResponseEntity respEntity = new ResponseEntity(response,
                    HttpStatus.NOT_FOUND);
            return respEntity;
        }
        Map response = new HashMap();
        Optional<PropertyDetails> tempPropertyDetails = propertyDetailsRepository.findOneByIdPropertyAndTipeProperty(
                idProperty,
                vmCetakRumah.getTipeProperty().trim());
        String filename = "Simulasi_Pemesanan_Rumah_" +
                vmCetakRumah.getNamaProperty() + "_" + vmCetakRumah.getTipeProperty().trim() + ".pdf";
        String path = env.getProperty("storage.file") + "test.pdf";
        List<ViewModelCetakFormulirPemesananRumah> list = new ArrayList<>();
        list.add(vmCetakRumah);
        Map<String, Object> parameter = new HashMap<>();
        InputStream fotoProerty = new FileInputStream(
                env.getProperty("storage.images") + tempPropertyDetails.get().getListPhoto().get(0).getNamaPhoto());
        parameter.put("logo", getClass().getResourceAsStream("/static/img/favicon.png"));
        parameter.put("fotoProperty", fotoProerty);
        cetakFormulirPemesananRumah.writePdf(list, path, parameter);
        InputStream inputStream = new FileInputStream(path);
        byte[] out = org.apache.commons.io.IOUtils.toByteArray(inputStream);
        response.put("file", out);
        response.put("filename", filename);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/cetakFormulirPemesananManual", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<Map> cetakFormulirPemesananManual(
            @RequestBody ViewModelCetakFormulirPemesananRumah vmCetakRumah) throws IOException {
        if (vmCetakRumah.getNamaProperty().equalsIgnoreCase("")) {
            Map response = new HashMap();
            response.put("message", "File Not Found");
            ResponseEntity respEntity = new ResponseEntity(response,
                    HttpStatus.NOT_FOUND);
            return respEntity;
        }
        Map response = new HashMap();
        String filename = "Simulasi_Pemesanan_Rumah_" +
                vmCetakRumah.getNamaProperty() + "_" + vmCetakRumah.getTipeProperty().trim() + ".pdf";
        String path = env.getProperty("storage.file") + "test.pdf";
        List<ViewModelCetakFormulirPemesananRumah> list = new ArrayList<>();
        list.add(vmCetakRumah);
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("logo", getClass().getResourceAsStream("/static/img/favicon.png"));
        cetakFormulirPemesananRumah.writePdfManual(list, path, parameter);
        InputStream inputStream = new FileInputStream(path);
        byte[] out = org.apache.commons.io.IOUtils.toByteArray(inputStream);
        response.put("file", out);
        response.put("filename", filename);
        return new ResponseEntity(response, HttpStatus.OK);
    }
    
}
