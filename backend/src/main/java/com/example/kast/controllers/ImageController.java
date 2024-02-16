package com.example.kast.controllers;


import com.example.kast.exceptions.AppException;
import com.example.kast.services.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;


/**
 * Данный REST-контроллер отвечает за обработку запросов, связанных с изображениями
 *
 * @param imageService объект класса {@link ImageService} - сервис, обрабатывающий запросы, связанные с изображениями
 * @author Кирилл "Tamada" Симовин
 */
@RestController
public record ImageController(ImageService imageService) {
    /**
     * Метод обрабатывает POST-запрос по пути "/uploadImage". Используется при загрузке изображений на сервер
     *
     * @param imageFile загружаемое изображение
     * @param imageName исходное название загружаемого файла
     * @param name      название турнира, или ник игрока, или название команды, для которого(ой) загружается изображение
     * @param path      путь до каталога на сервере, где будет расположено загружаемое изображение
     * @return <code>ResponseEntity</code> со статусом 201, тело которого - название файла на сервере
     * @throws AppException В случаях, если не получилось загрузить изображение
     */
    @PostMapping(value = "/uploadImage")
    public ResponseEntity<String> uploadImage(@RequestParam("imageFile") MultipartFile imageFile,
                                              @RequestParam("imageName") String imageName,
                                              @RequestParam("path") String path,
                                              @RequestParam("name") String name) throws AppException {
        String response = imageService.uploadImage(imageFile, imageName, path, name);
        return ResponseEntity.created(URI.create("/" + response)).body(response);
    }


    /**
     * Метод обрабатывает GET-запрос по пути "/getImage/{name}/{type}". Используется для получения изображения с сервера
     *
     * @param name название турнира, или ник игрока, или название команды, для которого(ой) запрашивается изображение
     * @param type тип запрашиваемого изображения, позволяет определить какое конкретно изображение запрашивается.
     *             Может принимать следующие значения:
     *             <li><b>header</b> - заголовок турнира</li>
     *             <li><b>trophy</b> - изображение трофея турнира</li>
     *             <li><b>other</b> - остальные изображения</li>
     * @return <code>ResponseEntity</code> со статусом 200, заголовками <code>Content-Length</code> и
     * <code>Content-Type</code>, тело которого - запрашиваемое изображение в виде массива байтов
     * @throws IOException  В случаях, когда не получается считать байты из файла
     * @throws AppException В случаях, если передан неизвестный параметр <i>name</i>
     */
    @GetMapping(value = "/getImage/{name}/{type}")
    public ResponseEntity<byte[]> getImage(@PathVariable("name") String name,
                                           @PathVariable("type") String type) throws IOException, AppException {
        return imageService.getImage(name, type);
    }
}
