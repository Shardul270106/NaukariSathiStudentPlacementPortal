package com.spp.demo.services;

import com.spp.demo.dto.DashboardSummaryDTO;
import com.spp.demo.models.JobApplication;
import com.spp.demo.models.PlacementRecord;
import com.spp.demo.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PlacementRecordRepository placementRecordRepository;

    @Autowired
    private JobRepo jobRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    public DashboardSummaryDTO getSummary() {
        DashboardSummaryDTO dto = new DashboardSummaryDTO();

        // ── Core counts ──
        dto.setTotalStudents(userRepository.count());
        dto.setTotalPlaced(placementRecordRepository.count());
        dto.setActiveJobs(jobRepository.countByStatus("Active"));
        dto.setTotalApplications(jobApplicationRepository.count());

        // ── Placement trend (monthly, current year) ──
        List<PlacementRecord> allPlacements = placementRecordRepository.findAll();
        List<String> monthLabels = Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
        Map<Integer, Long> byMonth = allPlacements.stream()
                .filter(p -> p.getOfferDate() != null)
                .collect(Collectors.groupingBy(p -> p.getOfferDate().getMonthValue(), Collectors.counting()));
        List<Long> trendData = new ArrayList<>();
        for (int m = 1; m <= 12; m++) trendData.add(byMonth.getOrDefault(m, 0L));
        dto.setPlacementTrend(new DashboardSummaryDTO.PlacementTrendDTO(monthLabels, trendData));

        // ── Placement type split ──
        long onCampus = allPlacements.stream().filter(p -> "ON_CAMPUS".equals(p.getPlacementType())).count();
        long offCampus = allPlacements.stream().filter(p -> "OFF_CAMPUS".equals(p.getPlacementType())).count();
        dto.setPlacementTypeSplit(new DashboardSummaryDTO.PlacementTypeSplitDTO(onCampus, offCampus));

        // ── Application status split ──
        List<JobApplication> allApps = jobApplicationRepository.findAll();
        Map<String, Long> statusSplit = allApps.stream()
                .filter(a -> a.getStatus() != null)
                .collect(Collectors.groupingBy(JobApplication::getStatus, Collectors.counting()));
        dto.setApplicationStatusSplit(statusSplit);

        // ── Package distribution ──
        List<String> pkgLabels = Arrays.asList("<4 LPA", "4–8 LPA", "8–12 LPA", "12–20 LPA", "20+ LPA");
        long lt4   = allPlacements.stream().filter(p -> p.getPackageLpa() != null && p.getPackageLpa() < 4).count();
        long lt8   = allPlacements.stream().filter(p -> p.getPackageLpa() != null && p.getPackageLpa() >= 4  && p.getPackageLpa() < 8).count();
        long lt12  = allPlacements.stream().filter(p -> p.getPackageLpa() != null && p.getPackageLpa() >= 8  && p.getPackageLpa() < 12).count();
        long lt20  = allPlacements.stream().filter(p -> p.getPackageLpa() != null && p.getPackageLpa() >= 12 && p.getPackageLpa() < 20).count();
        long gt20  = allPlacements.stream().filter(p -> p.getPackageLpa() != null && p.getPackageLpa() >= 20).count();
        dto.setPackageDistribution(new DashboardSummaryDTO.PackageDistributionDTO(pkgLabels, Arrays.asList(lt4, lt8, lt12, lt20, gt20)));

        // ── Branch-wise placement (from StudentProfile) ──
        Map<String, Long> branchMap = studentProfileRepository.findAll().stream()
                .filter(sp -> sp.getBranch() != null)
                .collect(Collectors.groupingBy(sp -> sp.getBranch().toUpperCase(), Collectors.counting()));
        List<Map.Entry<String, Long>> sortedBranches = branchMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(6).collect(Collectors.toList());
        dto.setBranchPlacement(new DashboardSummaryDTO.BranchPlacementDTO(
                sortedBranches.stream().map(Map.Entry::getKey).collect(Collectors.toList()),
                sortedBranches.stream().map(Map.Entry::getValue).collect(Collectors.toList())
        ));

        // ── Job categories ──
        Map<String, Long> catMap = jobRepository.findAll().stream()
                .filter(j -> j.getJobCategory() != null)
                .collect(Collectors.groupingBy(j -> {
                    String cat = j.getJobCategory().toLowerCase();
                    if (cat.contains("software") || cat.contains("it") || cat.contains("developer")) return "IT / Software";
                    if (cat.contains("finance") || cat.contains("bank")) return "Finance";
                    if (cat.contains("engineer") || cat.contains("core")) return "Core Engg";
                    if (cat.contains("sales") || cat.contains("marketing")) return "Sales & Mktg";
                    if (cat.contains("consult")) return "Consulting";
                    return "Others";
                }, Collectors.counting()));
        List<Map.Entry<String, Long>> sortedCats = catMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()).collect(Collectors.toList());
        dto.setJobCategories(new DashboardSummaryDTO.JobCategoryDTO(
                sortedCats.stream().map(Map.Entry::getKey).collect(Collectors.toList()),
                sortedCats.stream().map(Map.Entry::getValue).collect(Collectors.toList())
        ));

        // ── Top hiring companies ──
        Map<String, Long> companyMap = allPlacements.stream()
                .filter(p -> p.getCompanyName() != null)
                .collect(Collectors.groupingBy(PlacementRecord::getCompanyName, Collectors.counting()));
        List<DashboardSummaryDTO.TopCompanyDTO> topCompanies = companyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new DashboardSummaryDTO.TopCompanyDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        dto.setTopCompanies(topCompanies);

        // ── Recent placements (latest 5) ──
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<DashboardSummaryDTO.RecentPlacementDTO> recentPlacements = allPlacements.stream()
                .filter(p -> p.getOfferDate() != null)
                .sorted(Comparator.comparing(PlacementRecord::getOfferDate).reversed())
                .limit(5)
                .map(p -> {
                    String name = p.getStudentName() != null ? p.getStudentName() : "Student #" + p.getUserId();
                    return new DashboardSummaryDTO.RecentPlacementDTO(
                            name, p.getCompanyName(), p.getJobRole(),
                            p.getPackageLpa(), p.getPlacementType(),
                            p.getOfferDate().format(df)
                    );
                }).collect(Collectors.toList());
        dto.setRecentPlacements(recentPlacements);

        // ── Recent applications (latest 5) ──
        List<DashboardSummaryDTO.RecentApplicationDTO> recentApplications = allApps.stream()
                .filter(a -> a.getAppliedAt() != null)
                .sorted(Comparator.comparing(JobApplication::getAppliedAt).reversed())
                .limit(5)
                .map(a -> new DashboardSummaryDTO.RecentApplicationDTO(
                        a.getId(), a.getJobId(), a.getStatus(), a.getAiScore(),
                        a.getAppliedAt().toString(), a.getMatchedSkills()
                )).collect(Collectors.toList());
        dto.setRecentApplications(recentApplications);

        return dto;
    }
}
