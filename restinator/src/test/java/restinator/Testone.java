package restinator;

import java.io.IOException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import com.jive.qa.restinator.Endpoint;
import com.jive.qa.restinator.codecs.DefaultStringCodec;

@Slf4j
public class Testone
{

  @Test
  public void test() throws IOException
  {
    URL url = new URL("http://google.com");
    Endpoint<String, String> endpoint = new Endpoint<String, String>(new DefaultStringCodec());

    String content = endpoint.url(url, "services").get();
    log.debug(content);
  }

}
