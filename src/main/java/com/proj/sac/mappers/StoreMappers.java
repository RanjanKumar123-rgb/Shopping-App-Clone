package com.proj.sac.mappers;

import com.proj.sac.entity.Store;
import com.proj.sac.requestdto.StoreRequest;
import org.springframework.stereotype.Component;

@Component
public class StoreMappers {
    public Store mapToStore(StoreRequest storeRequest) {
        return Store.builder().storeName(storeRequest.getStoreName()).about(storeRequest.getAbout()).build();
    }
}
