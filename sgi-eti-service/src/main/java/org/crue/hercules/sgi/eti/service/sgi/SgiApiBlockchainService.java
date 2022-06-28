package org.crue.hercules.sgi.eti.service.sgi;

import java.util.HashMap;
import java.util.Map;

import org.crue.hercules.sgi.eti.config.BlockchainApiProperties;
import org.crue.hercules.sgi.eti.dto.blockchain.BlockchainOutput;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiBlockchainService {
  private final BlockchainApiProperties blockchainApiProperties;
  private final RestTemplate restTemplate;

  protected SgiApiBlockchainService(BlockchainApiProperties blockchainApiProperties, RestTemplate restTemplate) {
    this.blockchainApiProperties = blockchainApiProperties;
    this.restTemplate = restTemplate;
  }

  public String sellarDocumento(String hash) {
    log.debug("sellarDocumento(String hash) - start");
    String relativeUrl = "/dev/herc-timestamp-timestamp?token={pass}&data={data}";

    Map<String, String> params = new HashMap<>();
    params.put("pass", blockchainApiProperties.getPassword());
    params.put("data", hash);

    HttpMethod httpMethod = HttpMethod.POST;

    String mergedURL = new StringBuilder(blockchainApiProperties.getUrl()).append(relativeUrl).toString();
    BlockchainOutput response = null;
    try {
      response = restTemplate.exchange(mergedURL, httpMethod, null,
          new ParameterizedTypeReference<BlockchainOutput>() {
          }, params).getBody();
    } catch (Exception e) {
      log.error("sellarDocumento(String hash) - error", e);
    }

    log.debug("sellarDocumento(String hash) - end");

    if (response != null && response.getStatus().equals("success")) {
      return response.getTx();
    } else {
      return null;
    }
  }
}
