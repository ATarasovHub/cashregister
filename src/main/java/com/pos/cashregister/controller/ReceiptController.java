package com.pos.cashregister.controller;

import com.pos.cashregister.model.Receipt;
import com.pos.cashregister.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping
    public String getReceiptsPage(Model model) {
        List<Receipt> receipts = receiptService.getAllReceipts();
        log.info("Loaded {} receipts for receipts page", receipts.size());
        model.addAttribute("receipts", receipts);
        return "receipts";
    }

    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        List<Receipt> receipts = receiptService.getAllReceipts();
        log.info("Returning {} receipts via API", receipts.size());
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Receipt> getReceiptById(@PathVariable Long id) {
        Optional<Receipt> receipt = receiptService.getReceiptById(id);
        if (receipt.isPresent()) {
            log.info("Found receipt with id {}: {}", id, receipt.get());
            return ResponseEntity.ok(receipt.get());
        } else {
            log.warn("Receipt with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createReceipt(@RequestBody Receipt receipt) {
        log.info("Received receipt data: {}", receipt);
        try {
            Receipt savedReceipt = receiptService.createReceipt(receipt);
            log.info("Created receipt: {}", savedReceipt);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReceipt);
        } catch (Exception e) {
            log.error("Error creating receipt: {}", e.getMessage(), e);
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
            log.info("Deleting receipt with id {}", id);
            receiptService.deleteReceipt(id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("Attempted to delete non-existent receipt with id {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
