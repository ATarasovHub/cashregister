package com.pos.cashregister.controller;

import com.pos.cashregister.service.ProductService;
import com.pos.cashregister.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PosController {

    private final ProductService productService;
    private final ReceiptService receiptService;

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
}
