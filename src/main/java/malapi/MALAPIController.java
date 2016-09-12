package malapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@RestController
public class MALAPIController {
	
    
    @RequestMapping("/")
    public String index() {
        return "Thanks for using anime enquiry services please use it as follows:    /getMAL/{AnimeTitle}";
    }
    
    
    //create Access-Control-Allow-Origin for all responses.
    @ModelAttribute
    public void setVaryResponseHeader(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "https://www.nyaa.se");
    } 
    

    @RequestMapping(value="/getMAL/{title}", method=RequestMethod.GET)
	public  MAnimeContent getAnime(@PathVariable String title) throws Exception{
    	return setContent(sendGet(title));
    }
    
    
    private MAnimeContent setContent(String xmldoc) throws ParserConfigurationException, SAXException, IOException {
    	
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	
    	//String to xml 
    	dBuilder = dbFactory.newDocumentBuilder();  
    	Document doc = dBuilder.parse( new InputSource( new StringReader( xmldoc ) ));
          
    	MAnimeContent ac = new MAnimeContent();   
          
    	NodeList nList1= doc.getElementsByTagName("title");
        Node node = nList1.item(0);
    	ac.setName(node.getTextContent());

    	NodeList nList2= doc.getElementsByTagName("score");
      	ac.setScore(Double.parseDouble(nList2.item(0).getTextContent()));
        
    	NodeList nList3= doc.getElementsByTagName("status");
      	ac.setStatus(nList3.item(0).getTextContent());
        
    	NodeList nList4= doc.getElementsByTagName("image");
      	ac.setPic(nList4.item(0).getTextContent());
      	
      	NodeList nList5= doc.getElementsByTagName("id");
      	ac.setId(nList5.item(0).getTextContent());
      	
    	org.jsoup.nodes.Document document =Jsoup.connect("https://myanimelist.net/anime/"+ac.getId()).get();
        ac.setScoredby(document.getElementsByAttributeValue("itemprop", "ratingCount").html());
        ac.setGenre(document.select("a[href^=/anime/genre/]").html().replace("\n", ","));
      	return ac;
    }
    
    
	private String sendGet(String name) throws Exception {

		String url = "https://myanimelist.net/api/anime/search.xml?q="+name;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header  //TODO ADD LOGIN INFORMATION
		con.setRequestProperty("Authorization", "Basic ???????????????????????????????=");
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();

	}
    
    
    
}
