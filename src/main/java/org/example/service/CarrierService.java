package org.example.service;

import org.example.dto.carrier.CarrierCreateDto;
import org.example.exception.NotFoundException;
import org.example.model.Carrier;
import org.example.repository.CarrierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarrierService {
    private final CarrierRepository carrierRepository;

    public CarrierService(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
    }

    public Carrier createCarrier(CarrierCreateDto dto) {
        Carrier carrier = new Carrier();
        carrier.setName(dto.getName());
        carrier.setPhone(dto.getPhone());

        carrierRepository.save(carrier);
        return carrier;
    }

    public List<Carrier> getAllCarriers() {
        return carrierRepository.findAll();
    }

    public Carrier updateCarrier(Long id, CarrierCreateDto dto) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Carrier not found"));

        carrier.setName(dto.getName());
        carrier.setPhone(dto.getPhone());

        carrierRepository.save(carrier);
        return carrier;
    }

    public void deleteCarrier(Long id) {
        if (!carrierRepository.findById(id).isPresent()) {
            throw new NotFoundException("Carrier not found");
        }
        carrierRepository.delete(id);
    }
}
