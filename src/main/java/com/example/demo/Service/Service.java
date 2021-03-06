package com.example.demo.Service;

import com.example.demo.Model.Vo;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class Service {
    Path UploadPath = Paths.get("upload");
    Path DownloadPath = Paths.get("download");

    public File UploadFile(MultipartFile multipartFile) throws IOException {

        try {
            Files.copy(multipartFile.getInputStream(), UploadPath.resolve(multipartFile.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
        } catch (FileAlreadyExistsException e) {
            e.printStackTrace();
        }
        return new File(UploadPath + "\\" + multipartFile.getOriginalFilename());

    }


    public JsonMapper csvJson(File input) throws IOException {
        String fileName = input.getName();
        String noExtension = fileName.substring(0, fileName.lastIndexOf("."));
        File output = new File(DownloadPath + "\\" + noExtension + ".json");
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper
                .typedSchemaFor(Vo.class)
                .withHeader()
                .withComments();
        MappingIterator<Vo> mappingIterator = csvMapper
                .readerWithSchemaFor(Vo.class)
                .with(csvSchema)
                .readValues(input);

        List<Vo> data = mappingIterator.readAll();
        JsonMapper mapper = new JsonMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(output, data);
        return mapper;
    }

    public XmlMapper transferFile(File input) throws IOException {
        String fileName = input.getName();
        String noExtension = fileName.substring(0, fileName.lastIndexOf("."));
        File output = new File(DownloadPath + "\\" + noExtension + ".xml");
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper
                .typedSchemaFor(Vo.class)
                .withHeader()
                .withComments();
        MappingIterator<Vo> mappingIterator = csvMapper
                .readerWithSchemaFor(Vo.class)
                .with(csvSchema)
                .readValues(input);

        List<Vo> data = mappingIterator.readAll();
        XmlMapper mapper = new XmlMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(output, data);
        return mapper;
    }

    public void DeleteFile() {
//        String fileName = input.getOriginalFilename();
//        String noExtension = fileName.substring(0, fileName.lastIndexOf("."));
//        File path = new File(UploadPath + "\\");
//        path.delete();
        FileSystemUtils.deleteRecursively(UploadPath.toFile());
        FileSystemUtils.deleteRecursively(DownloadPath.toFile());
    }

    public void init(){
        try{
            Files.createDirectory(UploadPath);
            Files.createDirectory(DownloadPath);
        }catch (IOException e){
            throw  new RuntimeException("Could not initialize storage");
        }
    }


    public StreamingResponseBody DownloadFile(String filename) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(DownloadPath + "\\" + filename);
        return outputStream -> FileCopyUtils.copy(inputStream, outputStream);
    }

//    public String GetFileName(File file){
//        String fileName = file.getName();
//        String noExtension = fileName.substring(0, fileName.lastIndexOf("."));
//        return noExtension;
//    }

//    public Stream<Path> listFile() throws IOException {
//        Path filepath = Paths.get("D:\\democsv\\upload");
//        Resource resource = new UrlResource(filepath.toUri());
//        System.out.println(filepath);
//        System.out.println(resource);
//
//        Stream<Path> pathStream = Files.walk(filepath).map(filepath::relativize);
//        pathStream.map(Path::toString).filter(f->f.endsWith(".xml")).forEach(System.out::println);
//        return pathStream;
//    }

    public List<String> listFile() {
        List<String> filenames = new ArrayList<>();
        File file = new File(DownloadPath.toString());
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            filenames.add(f.getName());
        }
        return filenames;
    }
}
