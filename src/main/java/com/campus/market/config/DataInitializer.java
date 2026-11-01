package com.campus.market.config;

import com.campus.market.entity.Item;
import com.campus.market.entity.User;
import com.campus.market.repository.ItemRepository;
import com.campus.market.repository.UserRepository;
import com.campus.market.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public DataInitializer(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Ensure Admin User Exists
        User admin = userRepository.findByEmail("admin@campus.edu").orElseGet(() -> {
            User u = new User(
                    "admin@campus.edu",
                    PasswordUtil.hashPassword("admin123"),
                    "Campus Administrator",
                    "ROLE_ADMIN"
            );
            return userRepository.save(u);
        });

        // 2. Ensure Student Demo User Exists
        User student = userRepository.findByEmail("student@campus.edu").orElseGet(() -> {
            User u = new User(
                    "student@campus.edu",
                    PasswordUtil.hashPassword("student123"),
                    "Aarav Sharma",
                    "ROLE_USER"
            );
            return userRepository.save(u);
        });

        // 3. Seed the 8 Demo Items if no items exist in database
        if (itemRepository.count() == 0) {
            seedDemoItems(admin, student);
        }
    }

    @Transactional
    public void resetDemoData() {
        // Delete all existing demo items
        List<Item> demoItems = itemRepository.findByIsDemoTrue();
        itemRepository.deleteAll(demoItems);

        User admin = userRepository.findByEmail("admin@campus.edu").orElse(null);
        User student = userRepository.findByEmail("student@campus.edu").orElse(null);

        seedDemoItems(admin, student);
    }

    private void seedDemoItems(User admin, User student) {
        Item item1 = new Item(
                "Engineering Mathematics by B.S. Grewal (44th Ed.)",
                "Books",
                new BigDecimal("450.00"),
                "Well-kept copy with light pencil notes in the first three chapters. No torn pages. Ideal for first and second year students.",
                "Aarav Sharma",
                "aarav.demo@campus.edu",
                "/uploads/demo/demo-textbook.jpg",
                student,
                true
        );

        Item item2 = new Item(
                "Casio fx-991ES Plus Scientific Calculator",
                "Electronics",
                new BigDecimal("650.00"),
                "Fully working, exam-approved model. Comes with the original slide cover. Battery replaced last month.",
                "Priya Patel",
                "priya.demo@campus.edu",
                "/uploads/demo/demo-calculator.jpg",
                student,
                true
        );

        Item item3 = new Item(
                "Engineering Drawing Kit with Mini Drafter",
                "Engineering Tools",
                new BigDecimal("550.00"),
                "Mini drafter, compass set, protractor and French curves in a protective carry case. Used for one semester only.",
                "Rohan Verma",
                "rohan.demo@campus.edu",
                "/uploads/demo/demo-drawingkit.jpg",
                student,
                true
        );

        Item item4 = new Item(
                "White Lab Coat – Size M",
                "Lab Supplies",
                new BigDecimal("250.00"),
                "Cotton lab coat, washed and ironed. Full sleeves with three front pockets. Suitable for chemistry and biology practicals.",
                "Sneha Rao",
                "sneha.demo@campus.edu",
                "/uploads/demo/demo-labcoat.jpg",
                student,
                true
        );

        Item item5 = new Item(
                "Let Us C by Yashavant Kanetkar",
                "Books",
                new BigDecimal("180.00"),
                "Classic C programming reference. Clean pages, slightly worn cover. Great starting guide for beginners.",
                "Kabir Singh",
                "kabir.demo@campus.edu",
                "/uploads/demo/demo-programming.jpg",
                student,
                true
        );

        Item item6 = new Item(
                "Laptop Cooling Pad + USB Hub",
                "Accessories",
                new BigDecimal("400.00"),
                "Dual-fan cooling pad with built-in 4-port USB hub. Fits laptops up to 15.6 inches comfortably.",
                "Ananya Gupta",
                "ananya.demo@campus.edu",
                "/uploads/demo/demo-laptopacc.jpg",
                student,
                true
        );

        Item item7 = new Item(
                "Drawing Instruments Box (Compass & Divider Set)",
                "Engineering Tools",
                new BigDecimal("220.00"),
                "Precision metal compass, divider, bow pens and lead holder. All pieces present, no rust.",
                "Vikram Malhotra",
                "vikram.demo@campus.edu",
                "/uploads/demo/demo-instruments.jpg",
                student,
                true
        );
        item7.setStatus("SOLD"); // Preserving the original "Sold" status for demo item 7

        Item item8 = new Item(
                "Stationery Bundle – Notebooks, Pens & Highlighters",
                "Stationery",
                new BigDecimal("150.00"),
                "Five unused A4 notebooks, ten gel pens and a set of four highlighters. Bought extra by mistake.",
                "Meera Joshi",
                "meera.demo@campus.edu",
                "/uploads/demo/demo-stationery.jpg",
                student,
                true
        );

        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5, item6, item7, item8));
    }
}

