package com.eq.charactertracker.base;

import com.eq.charactertracker.exception.ObjectNotFoundException;
import com.eq.charactertracker.repo.BaseRepo;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;

@Component
@Getter
public abstract class BaseServiceImpl<E extends BaseEntity, T> implements BaseService<E, T>{

    private final Class<E> entityClass = (Class<E>) returnedTypeClass(0);
    private final Class<T> dtoClass = (Class<T>) returnedTypeClass(1);

    private Class<?> returnedTypeClass(int index) {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<?>) parameterizedType.getActualTypeArguments()[index];
    }

    private final ModelMapper modelMapper;
    private final BaseRepo<E, Long> baseRepo;

    protected BaseServiceImpl(ModelMapper modelMapper, BaseRepo<E, Long> baseRepo) {
        this.baseRepo = baseRepo;
        this.modelMapper = modelMapper;
    }

    protected E dtoToEntity(T dto) {
        return modelMapper.map(dto, entityClass);
    }

    protected T entityToDTO(E entity) {
        return modelMapper.map(entity, dtoClass);
    }

    @Override
    public T getById(long id) {
        E entity = baseRepo.findById(id).orElseThrow(() -> new ObjectNotFoundException(id));
        return (T) entityToDTO(entity);
    }

    @Override
    @Transactional
    public T create(T tObject) {
        E entity = dtoToEntity(tObject);
        entity = baseRepo.save(entity);
        return (T) entityToDTO(entity);
    }

    @Override
    @Transactional
    public T update(T tObject, long id) {
        E entity = dtoToEntity(tObject);
        entity.setId(id);
        entity = baseRepo.save(entity);
        return (T) entityToDTO(entity);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        baseRepo.deleteById(id);
        if(baseRepo.findById(id).isPresent()){
            return false;
        }
        return true;
    }

}
