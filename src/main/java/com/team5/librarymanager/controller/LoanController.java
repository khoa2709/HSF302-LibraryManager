package com.team5.librarymanager.controller;

import com.team5.librarymanager.entity.Book;
import com.team5.librarymanager.entity.Loan;
import com.team5.librarymanager.entity.LoanStatus;
import com.team5.librarymanager.entity.User;
import com.team5.librarymanager.service.BookService;
import com.team5.librarymanager.service.LoanService;
import com.team5.librarymanager.service.UserService;
import com.team5.librarymanager.service.EmailService;
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

    @Autowired
    private EmailService emailService;

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
    public String showLoans(@RequestParam(value = "keyword", required = false, defaultValue = "") String kw, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        // staff xem tất cả các phiếu mượn
        if (user.getRole().equals("staff")) {
            if (kw.equals("")) {
                model.addAttribute("loans", loanService.findAll());
            } else {
                model.addAttribute("loans", loanService.searchLoans(kw));
            }
        } else {
            // User chỉ xem phiếu mượn của mình
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

            model.addAttribute("users", userService.findMembUsers());
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
                                    @RequestParam String borrowerName,
                                    @RequestParam(required = false) String newFullName,
                                    @RequestParam(required = false) String newUsername,
                                    @RequestParam(required = false) String newEmail,
                                    RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            String actualBorrower = borrowerName;
            // Nếu chọn thêm người dùng mới
            if ("new".equals(borrowerName)) {
                // Kiểm tra thông tin hợp lệ
                if (newFullName == null || newFullName.isBlank() || newUsername == null || newUsername.isBlank() || newEmail == null || newEmail.isBlank()) {
                    redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin người dùng mới!");
                    return "redirect:/loans/borrow/" + bookId;
                }
                // Kiểm tra username đã tồn tại chưa
                if (userService.findByUsername(newUsername) != null) {
                    redirectAttributes.addFlashAttribute("error", "Username đã tồn tại!");
                    return "redirect:/loans/borrow/" + bookId;
                }
                // Tạo user mới
                User newUser = new User();
                newUser.setFullName(newFullName);
                newUser.setUsername(newUsername);
                newUser.setEmail(newEmail);
                newUser.setPassword("123");
                newUser.setRole("member");
                newUser.setStatus(true);
                userService.save(newUser);
                actualBorrower = newUsername;
            }
            loanService.borrowBookAsStaff(bookId, dueDate, actualBorrower);
            // Gửi mail cho user có sẵn
            if (!"new".equals(borrowerName)) {
                User borrower = userService.findByUsername(actualBorrower);
                Book book = bookService.findById(bookId).orElse(null);
                if (borrower != null && book != null && borrower.getEmail() != null && !borrower.getEmail().isBlank()) {
                    emailService.sendLoanCompletionMail(borrower.getEmail(), book.getTitle(), dueDate);
                }
            }
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
