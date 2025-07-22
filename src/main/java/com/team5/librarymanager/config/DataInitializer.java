package com.team5.librarymanager.config;

import com.team5.librarymanager.entity.*;
import com.team5.librarymanager.service.BookService;
import com.team5.librarymanager.service.CategoryService;
import com.team5.librarymanager.service.LoanService;
import com.team5.librarymanager.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;
import java.util.Arrays;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initData(UserService userService, CategoryService categoryService, BookService bookService, LoanService loanService) {
        return args -> {
            // 1. Users
                User nttv = new User("Nguyễn Thị Tường Vy", "admin", "123", "admin", true);
                User ndk = new User("Nguyễn Đăng Khoa", "staff", "123", "staff", true);
                User nlv = new User("Nguyễn Ly Vi", "member1", "123", "member", true);
                User nht = new User("Nguyễn Hữu Thành", "member2", "123", "member", true);
                User nch = new User("Nguyễn Công Hậu", "member3", "123", "member", true);
                userService.saveAll(Arrays.asList(nttv,ndk,nlv,nht,nch));

            // 2. Categories
                Category c1 = new Category("Sách giáo khoa", "Dành cho học sinh cấp 1 đến cấp 3", true);
                Category c2 = new Category("Truyện thiếu nhi", "Truyện tranh và sách dành cho trẻ em", true);
                Category c3 = new Category("Khoa học", "Sách về khoa học tự nhiên, công nghệ", true);
                Category c4 = new Category("Văn học", "Tác phẩm văn học trong và ngoài nước", true);
                Category c5 = new Category("Lịch sử", "Sách về lịch sử Việt Nam và thế giới", true);
                categoryService.saveAll(Arrays.asList(c1, c2, c3, c4, c5));


            // 3. Books
                bookService.saveAll(Arrays.asList(
                    new Book("Toán lớp 5", "Nhiều tác giả", "NXB Giáo dục", 2021, c1, 10, true),
                    new Book("Ngữ Văn lớp 5", "Nhiều tác giả", "NXB Giáo dục", 2021, c1, 8, true),
                    new Book("Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "NXB Kim Đồng", 2019, c2, 5, true),
                    new Book("Vũ trụ và các hành tinh", "Trần Minh", "NXB Khoa học", 2020, c3, 3, true),
                    new Book("Truyện cổ tích Việt Nam", "Tổng hợp", "NXB Trẻ", 2018, c2, 6, false),
                    new Book("Đại chiến thế giới I", "Lịch sử tổng hợp", "NXB Chính trị", 2017, c5, 2, false)
                ));


            // 4. Loans
                Book b1 = bookService.findById(1L).orElse(null);
                Book b2 = bookService.findById(2L).orElse(null);
                Book b3 = bookService.findById(3L).orElse(null);
                Book b5 = bookService.findById(5L).orElse(null);
                loanService.saveAll(Arrays.asList(
                    new Loan(nlv, b1, LocalDate.of(2025,7,1), LocalDate.of(2025,7,10), LocalDate.of(2025,7,9), "returned"),
                    new Loan(nlv, b3, LocalDate.of(2025,7,2), LocalDate.of(2025,7,12), LocalDate.of(2025,7,11), "returned"),
                    new Loan(nht, b2, LocalDate.of(2025,7,10), LocalDate.of(2025,7,17), null, "borrowed"),
                    new Loan(nch, b5, LocalDate.of(2025,6,25), LocalDate.of(2025,7,5), null, "late")
                ));

        };
    }
} 