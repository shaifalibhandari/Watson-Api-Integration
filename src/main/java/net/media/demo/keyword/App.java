package net.media.demo.keyword;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;

public class App {
	public static void main(String[] args) throws org.bytedeco.javacv.FrameGrabber.Exception, MalformedURLException, IOException {
		NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding("2018-04-29",
				"9dfcbd1c-4ad8-4241-81c9-58882c05976c", "62BaLXpHyz5T");
		ConceptsOptions concepts = new ConceptsOptions.Builder().limit(1).build();
		CategoriesOptions categories = new CategoriesOptions();
		EntitiesOptions entities = new EntitiesOptions.Builder().emotion(true).sentiment(true).limit(2).build();
		Features features = new Features.Builder().concepts(concepts).categories(categories).entities(entities).build();

		try (FileInputStream inputStream = new FileInputStream("input2.txt")) {
			@SuppressWarnings("deprecation")
			String text = IOUtils.toString(inputStream);
			AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(text).features(features).build();
			AnalysisResults response = service.analyze(parameters).execute();
			// System.out.println("Concept:"+response.getConcepts().get(0).getText());
			System.out.println("Category:" + response.getCategories().get(0).getLabel());
			System.out.println("Entity:" + response.getEntities().get(0).getText());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Cut video into frames
		Java2DFrameConverter converter = new Java2DFrameConverter();
		FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(
				"Ski.mp4");
		String imagePath = "/keyword/images";
		frameGrabber.start();
		Frame frame;
		int imgNum = 0;
		try {
			for (int ii = 1; ii <= frameGrabber.getLengthInFrames(); ii++) {
				imgNum++;
				frameGrabber.setFrameNumber(ii);
				frame = frameGrabber.grab();
				BufferedImage bi = converter.convert(frame);
				String path = imagePath + File.separator + imgNum + ".jpg";
				ImageIO.write(bi, "jpg", new File(path));
				ii += 2;
			}
			frameGrabber.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
