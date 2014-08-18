package com.jive.qa.dreidel.spinnit.wiremock;

import static com.github.tomakehurst.wiremock.client.RequestPatternBuilder.*;

import java.util.List;

import lombok.Getter;

import com.github.tomakehurst.wiremock.client.HttpAdminClient;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.global.GlobalSettings;
import com.github.tomakehurst.wiremock.global.RequestDelaySpec;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.stubbing.ListStubMappingsResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.verification.FindRequestsResult;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.VerificationResult;
import com.google.common.net.HostAndPort;

public class WiremockConfigurator
{

  private Admin admin;
  @Getter
  private final HostAndPort hap;

  WiremockConfigurator(HostAndPort hap)
  {
    admin = new HttpAdminClient(hap.getHostText(), hap.getPort());
    this.hap = hap;
  }

  WiremockConfigurator(HostAndPort hap, String urlPathPrefix)
  {
    admin = new HttpAdminClient(hap.getHostText(), hap.getPort(), urlPathPrefix);
    this.hap = hap;
  }

  void setAdmin(Admin admin)
  {
    this.admin = admin;
  }

  public void givenThat(MappingBuilder mappingBuilder)
  {
    register(mappingBuilder);
  }

  public void stubFor(MappingBuilder mappingBuilder)
  {
    givenThat(mappingBuilder);
  }

  public void saveMappings()
  {
    admin.saveMappings();
  }

  public void resetMappings()
  {
    admin.resetMappings();
  }

  public void resetScenarios()
  {
    admin.resetScenarios();
  }

  public void resetToDefaultMappings()
  {
    admin.resetToDefaultMappings();
  }

  public void register(MappingBuilder mappingBuilder)
  {
    StubMapping mapping = mappingBuilder.build();
    admin.addStubMapping(mapping);
  }

  public ListStubMappingsResult allStubMappings()
  {
    return admin.listAllStubMappings();
  }

  public void verifyThat(RequestPatternBuilder requestPatternBuilder)
  {
    RequestPattern requestPattern = requestPatternBuilder.build();
    VerificationResult result = admin.countRequestsMatching(requestPattern);
    result.assertRequestJournalEnabled();

    if (result.getCount() < 1)
    {
      throw new VerificationException(requestPattern, find(allRequests()));
    }
  }

  public void verifyThat(int count, RequestPatternBuilder requestPatternBuilder)
  {
    RequestPattern requestPattern = requestPatternBuilder.build();
    VerificationResult result = admin.countRequestsMatching(requestPattern);
    result.assertRequestJournalEnabled();

    if (result.getCount() != count)
    {
      throw new VerificationException(requestPattern, count, find(allRequests()));
    }
  }

  public void verify(RequestPatternBuilder requestPatternBuilder)
  {
    verifyThat(requestPatternBuilder);
  }

  public void verify(int count, RequestPatternBuilder requestPatternBuilder)
  {
    verifyThat(count, requestPatternBuilder);
  }

  public List<LoggedRequest> find(RequestPatternBuilder requestPatternBuilder)
  {
    FindRequestsResult result = admin.findRequestsMatching(requestPatternBuilder.build());
    result.assertRequestJournalEnabled();
    return result.getRequests();
  }

  public List<LoggedRequest> findAll(RequestPatternBuilder requestPatternBuilder)
  {
    return find(requestPatternBuilder);
  }

  public void setGlobalFixedDelay(int milliseconds)
  {
    setGlobalFixedDelayVariable(milliseconds);
  }

  public void setGlobalFixedDelayVariable(int milliseconds)
  {
    GlobalSettings settings = new GlobalSettings();
    settings.setFixedDelay(milliseconds);
    admin.updateGlobalSettings(settings);
  }

  public void addDelayBeforeProcessingRequests(int milliseconds)
  {
    admin.addSocketAcceptDelay(new RequestDelaySpec(milliseconds));
  }

  public void addRequestProcessingDelay(int milliseconds)
  {
    addDelayBeforeProcessingRequests(milliseconds);
  }

}
