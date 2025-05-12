package com.rescue.communitytraining.controller;

import com.rescue.communitytraining.entity.*;
import com.rescue.communitytraining.repository.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Controller
public class DashboardController {

    @Autowired private TrainingDataRepository trainingDataRepository;
    @Autowired private DistrictRepository districtRepository;


    @GetMapping("/dashboard")
    public String showDashboard(Model model,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) Long district,HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<District> districts = districtRepository.findAll();
        List<TrainingData> data = trainingDataRepository.findAll();

        boolean isFiltered = (startDate != null || endDate != null || district != null);


        if (startDate != null) {
            data = data.stream()
                    .filter(d -> !d.getDate().isBefore(startDate))
                    .toList();
        }

        if (endDate != null) {
            data = data.stream()
                    .filter(d -> !d.getDate().isAfter(endDate))
                    .toList();
        }

        if (district != null) {
            data = data.stream()
                    .filter(d -> d.getDistrict().getId().equals(district))
                    .toList();
        }




//        if (startDate != null && endDate != null && district != null ) {
//            data = trainingDataRepository.findByDateBetweenAndDistrictId(startDate, endDate, district);
//        }


        int trained = data.stream().mapToInt(TrainingData::getTrained).sum();
        int education = data.stream().mapToInt(TrainingData::getEducation).sum();
        int religious = data.stream().mapToInt(TrainingData::getReligious).sum();
        int other = data.stream().mapToInt(TrainingData::getOther).sum();
        int cert = data.stream().mapToInt(TrainingData::getCert).sum();
        int rescue_scout = data.stream().mapToInt(TrainingData::getRescue_scout).sum();

        model.addAttribute("trained", trained);
        model.addAttribute("education", education);
        model.addAttribute("religious", religious);
        model.addAttribute("other", other);
        model.addAttribute("cert", cert);
        model.addAttribute("rescue_scout", rescue_scout);
        model.addAttribute("districts", districts);
        model.addAttribute("data", data);
        model.addAttribute("isFiltered", isFiltered);
        return "dashboard";
    }

//    @GetMapping("/admin")
//    public String addForm(Model model) {
//        model.addAttribute("trainingData", new TrainingData());
//        model.addAttribute("districts", districtRepository.findAll());
//        return "admin";
//    }


    @GetMapping("/admin")
    public String addForm(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        System.out.println("User in session: " + session.getAttribute("loggedInUser"));
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("userDistrict", loggedInUser.getDistrict());
        model.addAttribute("trainingData", new TrainingData());
//        model.addAttribute("districts", districtRepository.findAll());
        return "admin";
    }

    @PostMapping("/admin/save")
    public String saveEntry(@ModelAttribute TrainingData trainingData, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        trainingData.setDistrict(user.getDistrict());
        trainingDataRepository.save(trainingData);
        return "redirect:/dashboard";
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=training_data.xlsx");

        List<TrainingData> data = trainingDataRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TrainingData");
        Row header = sheet.createRow(0);
        String[] columns = {"Date", "District", "Trained", "Education", "Religious", "Other","No. of CERTs Mobilized", "No. of Rescue Scouts"};
        for (int i = 0; i < columns.length; i++) header.createCell(i).setCellValue(columns[i]);

        int rowIdx = 1;
        for (TrainingData d : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(d.getDate().toString());
            row.createCell(1).setCellValue(d.getDistrict().getName());
            row.createCell(2).setCellValue(d.getTrained());
            row.createCell(3).setCellValue(d.getEducation());
            row.createCell(4).setCellValue(d.getReligious());
            row.createCell(5).setCellValue(d.getOther());
            row.createCell(5).setCellValue(d.getCert());
            row.createCell(5).setCellValue(d.getRescue_scout());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=training_data.pdf");

        List<TrainingData> data = trainingDataRepository.findAll();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            PdfPTable table = new PdfPTable(6);
            Stream.of("Date", "District", "Trained", "Education", "Religious", "Other","No. of CERTs Mobilized", "No. of Rescue Scouts")
                  .forEach(headerTitle -> {
                      PdfPCell header = new PdfPCell();
                      header.setPhrase(new Phrase(headerTitle));
                      table.addCell(header);
                  });
            for (TrainingData d : data) {
                System.out.println(d.getDate().toString()+d.getDistrict().getName()+String.valueOf(d.getTrained())+d.getEducation()+d.getReligious()+String.valueOf(d.getOther()));
                table.addCell(d.getDate().toString());
                table.addCell(d.getDistrict().getName());
                table.addCell(String.valueOf(d.getTrained()));
                table.addCell(String.valueOf(d.getEducation()));
                table.addCell(String.valueOf(d.getReligious()));
                table.addCell(String.valueOf(d.getOther()));
                table.addCell(String.valueOf(d.getCert()));
                table.addCell(String.valueOf(d.getRescue_scout()));
            }
            document.add(table);
        } catch (DocumentException e) {
            throw new IOException(e);
        } finally {
            document.close();
        }
    }
}