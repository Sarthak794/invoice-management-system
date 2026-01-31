package invoice.controller;

import invoice.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute("totalInvoices", dashboardService.totalInvoices());
        model.addAttribute("totalRevenue", dashboardService.totalRevenue());
        model.addAttribute("totalPaid", dashboardService.totalPaid());
        model.addAttribute("totalOutstanding", dashboardService.totalOutstanding());
        model.addAttribute("recentInvoices", dashboardService.recentInvoices());

        // 🔴 THIS LINE WAS MISSING
        model.addAttribute("content", "dashboard");

        // 🔴 THIS WAS WRONG BEFORE
        return "layout/base";
    }
}

    

