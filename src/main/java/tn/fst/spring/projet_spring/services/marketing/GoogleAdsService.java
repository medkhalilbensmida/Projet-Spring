package tn.fst.spring.projet_spring.services.marketing;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v19.resources.Campaign;
import com.google.ads.googleads.v19.resources.CampaignBudget;
import com.google.ads.googleads.v19.resources.Campaign.NetworkSettings;
import com.google.ads.googleads.v19.resources.AdGroup;
import com.google.ads.googleads.v19.resources.AdGroupAd;
import com.google.ads.googleads.v19.resources.Ad;
import com.google.ads.googleads.v19.enums.AdvertisingChannelTypeEnum.AdvertisingChannelType;
import com.google.ads.googleads.v19.enums.BudgetDeliveryMethodEnum.BudgetDeliveryMethod;
import com.google.ads.googleads.v19.enums.CampaignStatusEnum.CampaignStatus;
import com.google.ads.googleads.v19.errors.GoogleAdsException;
import com.google.ads.googleads.v19.enums.AdGroupStatusEnum.AdGroupStatus;
import com.google.ads.googleads.v19.common.ManualCpc;
import com.google.ads.googleads.v19.common.TextAdInfo;
import com.google.ads.googleads.v19.services.CampaignBudgetOperation;
import com.google.ads.googleads.v19.services.CampaignBudgetServiceClient;
import com.google.ads.googleads.v19.services.CampaignOperation;
import com.google.ads.googleads.v19.services.AdGroupOperation;
import com.google.ads.googleads.v19.services.AdGroupAdOperation;
import com.google.ads.googleads.v19.services.MutateCampaignBudgetsRequest;
import com.google.ads.googleads.v19.services.MutateCampaignBudgetsResponse;
import com.google.ads.googleads.v19.services.MutateCampaignsRequest;
import com.google.ads.googleads.v19.services.MutateCampaignsResponse;
import com.google.api.client.util.Data;
import com.google.ads.googleads.v19.services.MutateAdGroupsRequest;
import com.google.ads.googleads.v19.services.MutateAdGroupAdsRequest;
import com.google.ads.googleads.v19.services.CampaignServiceClient;
import com.google.ads.googleads.v19.services.AdGroupServiceClient;
import com.google.ads.googleads.v19.services.AdGroupAdServiceClient;
import com.google.ads.googleads.v19.services.GoogleAdsServiceClient;

import tn.fst.spring.projet_spring.model.marketing.Advertisement;
import tn.fst.spring.projet_spring.model.marketing.AdvertisementChannel;
import tn.fst.spring.projet_spring.model.marketing.config.GoogleAdsConfig;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoogleAdsService {

    @Autowired
    private GoogleAdsClient googleAdsClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private String addCampaignBudget(String customerId, String budgetName, long amountMicros) 
            throws GoogleAdsException {
        CampaignBudget budget =
                CampaignBudget.newBuilder()
                        .setName(budgetName)
                        .setDeliveryMethod(BudgetDeliveryMethod.STANDARD)
                        .setAmountMicros(amountMicros)
                        .build();

        CampaignBudgetOperation op = CampaignBudgetOperation.newBuilder().setCreate(budget).build();

        try (CampaignBudgetServiceClient campaignBudgetServiceClient =
                googleAdsClient.getLatestVersion().createCampaignBudgetServiceClient()) {
            MutateCampaignBudgetsResponse response =
                    campaignBudgetServiceClient.mutateCampaignBudgets(
                            customerId, List.of(op));
            return response.getResults(0).getResourceName();
        }
    }

    private Campaign createCampaign(String customerId, String campaignName, long budgetAmountMicros) throws GoogleAdsException {
        // Creates a budget to be used by the campaign.
        String budgetResourceName = addCampaignBudget(customerId, 
                "Budget for " + campaignName, budgetAmountMicros);

        // Configures the campaign network options
        NetworkSettings networkSettings =
                NetworkSettings.newBuilder()
                        .setTargetGoogleSearch(true)
                        .setTargetSearchNetwork(true)
                        .setTargetContentNetwork(true)
                        .setTargetPartnerSearchNetwork(false)
                        .build();


        LocalDate today = LocalDate.now();
        String startDate = today.plusDays(1).format(DATE_FORMATTER);
        String endDate = today.plusDays(30).format(DATE_FORMATTER);


        // Creates the campaign.
        Campaign campaign =
                Campaign.newBuilder()
                        .setName(campaignName)
                        .setAdvertisingChannelType(AdvertisingChannelType.SEARCH)
                        .setStatus(CampaignStatus.PAUSED)
                        .setManualCpc(ManualCpc.newBuilder().build())
                        .setCampaignBudget(budgetResourceName)
                        .setNetworkSettings(networkSettings)
                        .setStartDate(startDate)
                        .setEndDate(endDate)
                        .build();

        CampaignOperation op = CampaignOperation.newBuilder().setCreate(campaign).build();

        try (CampaignServiceClient campaignServiceClient =
                googleAdsClient.getLatestVersion().createCampaignServiceClient()) {
            MutateCampaignsResponse response =
                    campaignServiceClient.mutateCampaigns(customerId, List.of(op));

            return response.getResults(0).getCampaign();
        }
        }


    // Create a Google Ads Ad Group
    private AdGroup createAdGroup(String customerId, String adGroupName, String campaignResourceName) {
        try (AdGroupServiceClient adGroupServiceClient = googleAdsClient.getLatestVersion().createAdGroupServiceClient()) {

            // Create the ad group
            AdGroup adGroup = AdGroup.newBuilder()
                    .setName(adGroupName)
                    .setCampaign(campaignResourceName)
                    .setStatus(AdGroupStatus.PAUSED)
                    .setCpcBidMicros(1000000)  // Set a bid (e.g., $1)
                    .build();

            // Create ad group operation
            AdGroupOperation adGroupOperation = AdGroupOperation.newBuilder()
                    .setCreate(adGroup)
                    .build();

            // Create the ad group
            return adGroupServiceClient.mutateAdGroups(
                MutateAdGroupsRequest.newBuilder()
                    .setCustomerId(customerId)
                    .addOperations(adGroupOperation)
                    .build()
            ).getResults(0).getAdGroup();
        }
    }

    // Create an ad for the Ad Group
    private Ad createAd(String customerId, String adGroupResourceName, String headline, String description, String finalUrl) {
        try (AdGroupAdServiceClient adGroupAdServiceClient = googleAdsClient.getLatestVersion().createAdGroupAdServiceClient()) {

            // Create the text ad
            TextAdInfo textAdInfo = TextAdInfo.newBuilder()
                    .setHeadline(headline)
                    .setDescription1(description)
                    .build();

            // Create the ad object
            Ad ad = Ad.newBuilder()
                    .setFinalUrls(0,finalUrl)
                    .setTextAd(textAdInfo)
                    .build();

            // Create the AdGroupAd
            AdGroupAd adGroupAd = AdGroupAd.newBuilder()
                    .setAdGroup(adGroupResourceName)
                    .setAd(ad)
                    .build();

            // Create ad group ad operation
            AdGroupAdOperation adGroupAdOperation = AdGroupAdOperation.newBuilder()
                    .setCreate(adGroupAd)
                    .build();

            // Create the ad
            return adGroupAdServiceClient.mutateAdGroupAds(
                MutateAdGroupAdsRequest.newBuilder()
                    .setCustomerId(customerId)
                    .addOperations(adGroupAdOperation)
                    .build()
            ).getResults(0).getAdGroupAd().getAd();
        }
    }


    public  Map<String, String> createChannelCampaign(AdvertisementChannel channel) {
        // Implementation for creating a channel
        GoogleAdsConfig config = channel.getGoogleAdsConfig();
        String customerId = config.getCustomerId();
        String campaignName = config.getCampaignName();
        Long compaignBudget = config.getCampaignBudgetMicros();
        String adGroupName = config.getAdGroupName();
        try
            {
                // Create a new campaign
                Campaign campaign = createCampaign(customerId, campaignName, compaignBudget); // Example budget

                // Create an ad group for the campaign
                AdGroup adGroup = createAdGroup(customerId, adGroupName, campaign.getResourceName());

                System.out.println("Ad group created with ID: " + adGroup.getResourceName());
                
                // return a Map containing the ad group resource name and the campaign resource name
                return Map.of(
                        "adGroupResourceName", adGroup.getResourceName(),
                        "campaignResourceName", campaign.getResourceName()
                );
            }
            catch (GoogleAdsException e) {
                System.err.println("Google Ads API request failed: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        return null;
    }


    public void sendAdFromChannel(Advertisement ad, AdvertisementChannel channel) {
            // Extract necessary information from the Channel entity
            GoogleAdsConfig config = channel.getGoogleAdsConfig();

            String customerId = config.getCustomerId();
            String campaignName = config.getCampaignName();
            Long compaignBudget = config.getCampaignBudgetMicros();
            String adGroupName = config.getAdGroupName();
            String headline = ad.getName();
            String description = ad.getDescription();

            String finalUrl = ad.getUrl().equals(null) ? ad.getUrl() : "https://www.example.com"; // Default URL if not provided

            try
            {
                // Create a new campaign
                Campaign campaign = createCampaign(customerId, campaignName, compaignBudget); // Example budget

                // Create an ad group for the campaign
                AdGroup adGroup = createAdGroup(customerId, adGroupName, campaign.getResourceName());

                // Create an ad for the ad group
                Ad createdAd = createAd(customerId, adGroup.getResourceName(), headline, description, finalUrl);

                System.out.println("Ad created with ID: " + createdAd.getResourceName());
            } catch (GoogleAdsException e) {
                System.err.println("Google Ads API request failed: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
            }



            
    }
}