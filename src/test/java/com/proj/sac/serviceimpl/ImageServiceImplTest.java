package com.proj.sac.serviceimpl;

import com.proj.sac.entity.Store;
import com.proj.sac.entity.StoreImage;
import com.proj.sac.enums.ImageType;
import com.proj.sac.exception.ImageNotFoundException;
import com.proj.sac.exception.StoreNotFoundException;
import com.proj.sac.repo.ImageRepo;
import com.proj.sac.repo.StoreRepo;
import com.proj.sac.util.ResponseStructure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

//Test Case done using Mockito
@ActiveProfiles("test")
class ImageServiceImplTest {

    @Mock
    private StoreRepo storeRepo;

    @Mock
    private ImageRepo imageRepo;

    @Mock
    private ResponseStructure<String> responseStructure;

    @Mock
    private ResponseStructure<MultipartFile> imageStructure;

    @InjectMocks
    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddStoreImage_Success() throws IOException {
        int storeId = 1;
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test image content".getBytes());

        Store store = new Store();
        store.setStoreId(storeId);

        StoreImage storeImage = new StoreImage();
        storeImage.setStoreId(storeId);
        storeImage.setImageType(ImageType.LOGO);
        storeImage.setContentType(image.getContentType());
        storeImage.setImageByte(image.getBytes());
        storeImage.setImageId("123");

        when(storeRepo.findById(storeId)).thenReturn(Optional.of(store));
        when(imageRepo.save(any(StoreImage.class))).thenReturn(storeImage);
        when(storeRepo.save(any(Store.class))).thenReturn(store);

        ResponseEntity<ResponseStructure<String>> response = imageService.addStoreImage(storeId, image);

        verify(imageRepo, times(1)).save(any(StoreImage.class));
        verify(storeRepo, times(1)).save(any(Store.class));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testAddStoreImage_StoreNotFound() {
        int storeId = 1;
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test image content".getBytes());

        when(storeRepo.findById(storeId)).thenReturn(Optional.empty());

        StoreNotFoundException exception = assertThrows(StoreNotFoundException.class, () -> {
            imageService.addStoreImage(storeId, image);
        });

        assertEquals("Store object not found !!!", exception.getMessage());
    }

    @Test
    void testGetStoreImage_Success() {
        String imageId = "123";
        StoreImage storeImage = new StoreImage();
        storeImage.setImageId(imageId);
        storeImage.setContentType("image/jpeg");
        storeImage.setImageByte("test image content".getBytes());

        when(imageRepo.findById(imageId)).thenReturn(Optional.of(storeImage));

        ResponseEntity<byte[]> response = imageService.getStoreImage(imageId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(storeImage.getContentType(), response.getHeaders().getContentType().toString());
        assertEquals(storeImage.getImageByte().length, response.getHeaders().getContentLength());
        assertEquals(storeImage.getImageByte(), response.getBody());
    }

    @Test
    void testGetStoreImage_ImageNotFound() {
        String imageId = "123";

        when(imageRepo.findById(imageId)).thenReturn(Optional.empty());

        ImageNotFoundException exception = assertThrows(ImageNotFoundException.class, () -> {
            imageService.getStoreImage(imageId);
        });

        assertEquals("Image not found !!!", exception.getMessage());
    }
}