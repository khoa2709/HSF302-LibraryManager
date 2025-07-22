package com.team5.librarymanager.controller;

import com.team5.librarymanager.entity.Book;
import com.team5.librarymanager.entity.Loan;
import com.team5.librarymanager.entity.LoanStatus;
import com.team5.librarymanager.entity.User;
import com.team5.librarymanager.service.BookService;
import com.team5.librarymanager.service.LoanService;
import com.team5.librarymanager.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService; 

    @GetMapping("/history")
    public String viewLoanHistory(Model model, Principal principal) {
    User user = userService.findByUsername(principal.getName());
    List<Loan> loans;
    if (user.getRole().equals("staff") || user.getRole().equals("admin")) {
        loans = loanService.findAll();
    } else {
        loans = loanService.getLoanHistoryByUser(user.getId());
    }
    model.addAttribute("loans", loans);
    return "loans"; // Sử dụng lại template loans.html
}

    @GetMapping("")
    public String showLoans(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        // staff xem tất cả các phiếu mượn
        if (user.getRole().equals("staff")) {
            model.addAttribute("loans", loanService.findAll());
        } else {
            // User thường chỉ xem phiếu mượn của mình
            model.addAttribute("loans", loanService.getLoansByUser(user.getId()));
        }
        
        return "loans";
    }

    @GetMapping("/borrow/{bookId}")
    public String showBorrowForm(@PathVariable Long bookId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Book book = bookService.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));
            
            if (!book.isStatus() || book.getQuantity() <= 0) {
                throw new IllegalStateException("Sách không khả dụng để mượn");
            }

            LocalDate today = LocalDate.now();
            LocalDate maxDueDate = today.plusDays(30); // Cho phép mượn tối đa 30 ngày

            model.addAttribute("book", book);
            model.addAttribute("today", today);
            model.addAttribute("maxDueDate", maxDueDate);

            return "loan-form";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "loan-form";
        }
    }

    @PostMapping("/borrow")
    public String processBorrowBook(@RequestParam Long bookId, 
                                  @RequestParam LocalDate dueDate,
                                  HttpSession session, 
                                  RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Book book = bookService.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));
            
            if (!book.isStatus() || book.getQuantity() <= 0) {
                throw new IllegalStateException("Sách không khả dụng để mượn");
            }

            // Kiểm tra ngày trả hợp lệ
            LocalDate today = LocalDate.now();
            LocalDate maxDueDate = today.plusDays(30);
            
            if (dueDate.isBefore(today) || dueDate.isAfter(maxDueDate)) {
                throw new IllegalStateException("Ngày trả không hợp lệ (tối đa 30 ngày)");
            }

            // Tạo phiếu mượn mới
            Loan loan = new Loan();
            loan.setUser(user);
            loan.setBook(book);
            loan.setLoanDate(today);
            loan.setDueDate(dueDate);
            loan.setStatus(LoanStatus.BORROWED);
            
            loanService.save(loan);
            
            // Giảm số lượng sách
            book.setQuantity(book.getQuantity() - 1);
            bookService.save(book);

            redirectAttributes.addFlashAttribute("success", "Mượn sách thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/loans";
    }

    @GetMapping("/return/{loanId}")
    public String returnBook(@PathVariable Long loanId, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Loan loan = loanService.findById(loanId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu mượn"));
            
            // Kiểm tra người dùng có quyền trả sách này không
            if (user.getRole().equals("member") && !loan.getUser().getId().equals(user.getId())) {
                throw new IllegalStateException("Bạn không có quyền trả sách này");
            }

            loanService.returnBook(loan);
            redirectAttributes.addFlashAttribute("success", "Trả sách thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi trả sách");
        }

        return "redirect:/loans";
    }

    @GetMapping("/current")
    public String showCurrentLoans(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole().equals("staff")) {
            model.addAttribute("loans", loanService.getCurrentLoans());
        } else {
            model.addAttribute("loans", loanService.getLoansByUserAndStatus(user.getId(), LoanStatus.BORROWED));
        }

        return "loans";
    }
}
