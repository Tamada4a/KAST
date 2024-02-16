package com.example.kast.services;


import com.example.kast.exceptions.AppException;
import com.example.kast.mongo_collections.documents.PlayerDoc;
import com.example.kast.mongo_collections.documents.TeamDoc;
import com.example.kast.mongo_collections.documents.TournamentDoc;
import com.example.kast.mongo_collections.interfaces.PlayerRepository;
import com.example.kast.mongo_collections.interfaces.TeamRepository;
import com.example.kast.mongo_collections.interfaces.TournamentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.kast.utils.Utils.replaceSpaces;
import static com.example.kast.utils.Utils.replaceDashes;


/**
 * Данный класс является сервисом, реализующим логику обработки изображений - загрузки, удаления, выгрузки
 *
 * @param teamRepository       интерфейс для взаимодействия с сущностями {@link TeamDoc}
 * @param playerRepository     интерфейс для взаимодействия с сущностями {@link PlayerDoc}
 * @param tournamentRepository интерфейс для взаимодействия с сущностями {@link TournamentDoc}
 * @author Кирилл "Tamada" Симовин
 */
@Service
public record ImageService(TeamRepository teamRepository, PlayerRepository playerRepository,
                           TournamentRepository tournamentRepository) {
    /**
     * Переменная содержит путь до директории с изображениями на сервере
     */
    private static final String imageDirectory = System.getProperty("user.dir") + "/images";


    /**
     * Метод позволяет загрузить изображение на сервер
     *
     * @param imageFile загружаемое изображение
     * @param imageName исходное название загружаемого файла
     * @param path      путь до каталога на сервере, где будет расположено загружаемое изображение
     * @param name      название турнира, или ник игрока, или название команды, для которого(ой) загружается изображение
     * @return Название файла на сервере
     * @throws AppException В случаях, если не получилось загрузить изображение
     */
    public String uploadImage(MultipartFile imageFile, String imageName, String path, String name) throws AppException {
        name = replaceDashes(name);
        imageName = changeFileName(imageName, name);

        if (teamRepository.existsByTeamName(name)) {
            TeamDoc teamDoc = teamRepository.findByTeamName(name);
            teamDoc.setLogoLink(path + imageName);
            teamRepository.save(teamDoc);
        } else if (playerRepository.existsByNick(name)) {
            PlayerDoc playerDoc = playerRepository.findByNick(name);
            playerDoc.setPhotoLink(path + imageName);
            playerRepository.save(playerDoc);
        } else if (tournamentRepository.existsByName(name)) {
            TournamentDoc tournamentDoc = tournamentRepository.findByName(name);
            switch (path) {
                case "/events_trophy/" -> tournamentDoc.setTrophyLink(path + imageName);
                case "/events_logo/" -> tournamentDoc.setLogoLink(path + imageName);
                case "/events_header/" -> tournamentDoc.setHeaderLink(path + imageName);
                default -> tournamentDoc.setMvpLink(path + imageName);
            }
            tournamentRepository.save(tournamentDoc);
        }

        makeDirectoryIfNotExist(imageDirectory + path);
        Path fileNamePath = Paths.get(imageDirectory + path, imageName);
        try {
            Files.write(fileNamePath, imageFile.getBytes());
            return imageName;
        } catch (IOException ex) {
            throw new AppException("Не получилось загрузить изображение", HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Метод позволяет получить изображение с сервера в виде массива байтов
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
    public ResponseEntity<byte[]> getImage(String name, String type) throws IOException, AppException {
        String path;

        name = replaceDashes(name);

        if (teamRepository.existsByTeamName(name)) {
            path = teamRepository.findByTeamName(name).getLogoLink();
        } else if (teamRepository.existsByTag(name)) {
            path = teamRepository.findByTag(name).getLogoLink();
        } else if (playerRepository.existsByNick(name)) {
            path = playerRepository.findByNick(name).getPhotoLink();
        } else if (tournamentRepository.existsByName(name)) {
            if (type.equals("trophy")) {
                path = tournamentRepository.findByName(name).getTrophyLink();
            } else if (type.equals("header")) {
                path = tournamentRepository.findByName(name).getHeaderLink();
            } else {
                path = tournamentRepository.findByName(name).getLogoLink();
            }
        } else if (name.startsWith("MVP ")) {
            name = name.replaceFirst("MVP ", "");
            path = tournamentRepository.findByName(name).getMvpLink();
        } else throw new AppException("Неизвестный параметр", HttpStatus.BAD_REQUEST);

        String mediaType = getMediaType(path);

        File file = new File(imageDirectory + path);

        byte[] fileContent = Files.readAllBytes(file.toPath());

        return ResponseEntity.ok().contentLength(fileContent.length).contentType(MediaType.parseMediaType(mediaType))
                .body(fileContent);
    }


    /**
     * Метод позволяет удалить файл с сервера
     *
     * @param path путь до удаляемого изображения
     * @return Результат удаления файла с сервера: <code>true</code>, если файл удален успешно; <code>false</code> иначе
     */
    public Boolean deleteImage(String path) {
        File file = new File(imageDirectory + path);
        return file.delete();
    }


    /**
     * Метод изменяет название файла на <i>name.расширение-imageName</i>. Например:<br></br>
     * исходное название файла - <i>file.svg</i>, <br></br>
     * название турнира - <i>Zasada Cup</i>. <br></br>
     * В результате получается <i>Zasada-Cup.svg</i>
     *
     * @param imageName исходное название загружаемого файла
     * @param name      название турнира, или ник игрока, или название команды, для которого(ой) загружается изображение
     * @return Название файла в формате <i>name.расширение-imageName</i> с замещением пробелов на "-"
     */
    public String changeFileName(String imageName, String name) {
        String extension = imageName.substring(imageName.lastIndexOf("."));
        return replaceSpaces(name + extension);
    }


    /**
     * Метод создает директорию по указанному пути, если таковой не существует
     *
     * @param imageDirectory путь до директории
     */
    private void makeDirectoryIfNotExist(String imageDirectory) {
        File directory = new File(imageDirectory);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }


    /**
     * Метод позволяет получить медиа тип файла
     *
     * @param path пусть до файла, чей медиа тип необходимо получить
     * @return Медиа тип файла
     */
    private String getMediaType(String path) {
        String extension = path.substring(path.lastIndexOf("."));

        return switch (extension) {
            case ".png" -> "image/png";
            case ".jpg" -> "image/jpg";
            case ".jpeg" -> "image/jpeg";
            case ".webp" -> "image/webp";
            case ".svg" -> "image/svg+xml";
            default -> "";
        };
    }
}
