package com.pharmacare.prescription;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PrescriptionStorageService {

    private final Path baseDir;

    public PrescriptionStorageService(@Value("${app.files.prescriptionsDir}") String prescriptionsDir) {
        this.baseDir = Paths.get(prescriptionsDir).toAbsolutePath().normalize();
    }

    public String save(byte[] bytes, String originalFilename) {
        try {
            Files.createDirectories(baseDir);
            String safeName = (originalFilename == null ? "file" : originalFilename).replaceAll("[^a-zA-Z0-9._-]", "_");
            String filename = UUID.randomUUID() + "_" + safeName;
            Path target = baseDir.resolve(filename);
            Files.write(target, bytes);
            return filename;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to save prescription file");
        }
    }
}
