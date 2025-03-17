package com.pos.cashregister.controller;

import com.pos.cashregister.model.Receipt;
import com.pos.cashregister.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping
    public String getReceiptsPage(Model model) {
        model.addAttribute("receipts", receiptService.getAllReceipts());
        return "receipts";
    }

    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Receipt> getReceiptById(@PathVariable Long id) {
        Optional<Receipt> receipt = receiptService.getReceiptById(id);
        return receipt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createReceipt(@RequestBody Receipt receipt) {
        try {
            Receipt savedReceipt = receiptService.createReceipt(receipt);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReceipt);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error creating receipt: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long id) {
        Optional<Receipt> existingReceipt = receiptService.getReceiptById(id);
        if (existingReceipt.isPresent()) {
            receiptService.deleteReceipt(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
