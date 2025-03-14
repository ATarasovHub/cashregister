package com.pos.cashregister.controller;

import com.pos.cashregister.service.ProductService;
import com.pos.cashregister.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PosController {

    private final ProductService productService;
    private final ReceiptService receiptService;

    @Autowired
    public PosController(ProductService productService, ReceiptService receiptService) {
        this.productService = productService;
        this.receiptService = receiptService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
}

