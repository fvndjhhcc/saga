package by.raikiri.order_creation_service.controller;

import by.raikiri.order_creation_service.dto.OrderDTO;
import by.raikiri.order_creation_service.service.OrderCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderCreationController {

    private final OrderCreationService service;

    @PostMapping("/create")
    public OrderDTO create() {
        return service.createOrder();
    }
}
