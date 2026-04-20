package com.spp.demo.dto;

import java.util.List;
import java.util.Map;

public class DashboardSummaryDTO {

    private long totalStudents;
    private long totalPlaced;
    private long activeJobs;
    private long totalApplications;

    private PlacementTrendDTO placementTrend;
    private PlacementTypeSplitDTO placementTypeSplit;
    private Map<String, Long> applicationStatusSplit;
    private PackageDistributionDTO packageDistribution;
    private BranchPlacementDTO branchPlacement;
    private JobCategoryDTO jobCategories;

    private List<TopCompanyDTO> topCompanies;
    private List<RecentPlacementDTO> recentPlacements;
    private List<RecentApplicationDTO> recentApplications;

    // ── Nested DTOs ──

    public static class PlacementTrendDTO {
        private List<String> labels;
        private List<Long> data;
        public PlacementTrendDTO(List<String> labels, List<Long> data) { this.labels = labels; this.data = data; }
        public List<String> getLabels() { return labels; }
        public List<Long> getData() { return data; }
    }

    public static class PlacementTypeSplitDTO {
        private long onCampus;
        private long offCampus;
        public PlacementTypeSplitDTO(long onCampus, long offCampus) { this.onCampus = onCampus; this.offCampus = offCampus; }
        public long getOnCampus() { return onCampus; }
        public long getOffCampus() { return offCampus; }
    }

    public static class PackageDistributionDTO {
        private List<String> labels;
        private List<Long> data;
        public PackageDistributionDTO(List<String> labels, List<Long> data) { this.labels = labels; this.data = data; }
        public List<String> getLabels() { return labels; }
        public List<Long> getData() { return data; }
    }

    public static class BranchPlacementDTO {
        private List<String> labels;
        private List<Long> data;
        public BranchPlacementDTO(List<String> labels, List<Long> data) { this.labels = labels; this.data = data; }
        public List<String> getLabels() { return labels; }
        public List<Long> getData() { return data; }
    }

    public static class JobCategoryDTO {
        private List<String> labels;
        private List<Long> data;
        public JobCategoryDTO(List<String> labels, List<Long> data) { this.labels = labels; this.data = data; }
        public List<String> getLabels() { return labels; }
        public List<Long> getData() { return data; }
    }

    public static class TopCompanyDTO {
        private String name;
        private long placements;
        public TopCompanyDTO(String name, long placements) { this.name = name; this.placements = placements; }
        public String getName() { return name; }
        public long getPlacements() { return placements; }
    }

    public static class RecentPlacementDTO {
        private String studentName;
        private String companyName;
        private String jobRole;
        private Double packageLpa;
        private String placementType;
        private String offerDate;
        public RecentPlacementDTO(String studentName, String companyName, String jobRole, Double packageLpa, String placementType, String offerDate) {
            this.studentName = studentName; this.companyName = companyName; this.jobRole = jobRole;
            this.packageLpa = packageLpa; this.placementType = placementType; this.offerDate = offerDate;
        }
        public String getStudentName() { return studentName; }
        public String getCompanyName() { return companyName; }
        public String getJobRole() { return jobRole; }
        public Double getPackageLpa() { return packageLpa; }
        public String getPlacementType() { return placementType; }
        public String getOfferDate() { return offerDate; }
    }

    public static class RecentApplicationDTO {
        private Long id;
        private Long jobId;
        private String status;
        private Integer aiScore;
        private String appliedAt;
        private String matchedSkills;
        public RecentApplicationDTO(Long id, Long jobId, String status, Integer aiScore, String appliedAt, String matchedSkills) {
            this.id = id; this.jobId = jobId; this.status = status;
            this.aiScore = aiScore; this.appliedAt = appliedAt; this.matchedSkills = matchedSkills;
        }
        public Long getId() { return id; }
        public Long getJobId() { return jobId; }
        public String getStatus() { return status; }
        public Integer getAiScore() { return aiScore; }
        public String getAppliedAt() { return appliedAt; }
        public String getMatchedSkills() { return matchedSkills; }
    }

    // ── Root getters/setters ──
    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
    public long getTotalPlaced() { return totalPlaced; }
    public void setTotalPlaced(long totalPlaced) { this.totalPlaced = totalPlaced; }
    public long getActiveJobs() { return activeJobs; }
    public void setActiveJobs(long activeJobs) { this.activeJobs = activeJobs; }
    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }
    public PlacementTrendDTO getPlacementTrend() { return placementTrend; }
    public void setPlacementTrend(PlacementTrendDTO placementTrend) { this.placementTrend = placementTrend; }
    public PlacementTypeSplitDTO getPlacementTypeSplit() { return placementTypeSplit; }
    public void setPlacementTypeSplit(PlacementTypeSplitDTO placementTypeSplit) { this.placementTypeSplit = placementTypeSplit; }
    public Map<String, Long> getApplicationStatusSplit() { return applicationStatusSplit; }
    public void setApplicationStatusSplit(Map<String, Long> applicationStatusSplit) { this.applicationStatusSplit = applicationStatusSplit; }
    public PackageDistributionDTO getPackageDistribution() { return packageDistribution; }
    public void setPackageDistribution(PackageDistributionDTO packageDistribution) { this.packageDistribution = packageDistribution; }
    public BranchPlacementDTO getBranchPlacement() { return branchPlacement; }
    public void setBranchPlacement(BranchPlacementDTO branchPlacement) { this.branchPlacement = branchPlacement; }
    public JobCategoryDTO getJobCategories() { return jobCategories; }
    public void setJobCategories(JobCategoryDTO jobCategories) { this.jobCategories = jobCategories; }
    public List<TopCompanyDTO> getTopCompanies() { return topCompanies; }
    public void setTopCompanies(List<TopCompanyDTO> topCompanies) { this.topCompanies = topCompanies; }
    public List<RecentPlacementDTO> getRecentPlacements() { return recentPlacements; }
    public void setRecentPlacements(List<RecentPlacementDTO> recentPlacements) { this.recentPlacements = recentPlacements; }
    public List<RecentApplicationDTO> getRecentApplications() { return recentApplications; }
    public void setRecentApplications(List<RecentApplicationDTO> recentApplications) { this.recentApplications = recentApplications; }
}
