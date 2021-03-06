package edu.iris.dmc.station.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.validation.ValidatorService;
import edu.iris.dmc.validation.ValidatorServiceImp;
import edu.iris.dmc.validation.validator.ResponseGroup;


//import org.testng.annotations.Test;

public class AllRulesTest {

	private InputStream in;
	private static Properties messages = new Properties();
	private static Validator validator;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();

		InputStream is = null;
		try {
			is = AllRulesTest.class.getClassLoader().getResourceAsStream("ValidationMessages.properties");
			messages.load(is);
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@After
	public void close() {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void networkCodeTooLong() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_lengthNETCODE.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);

		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(1, violations.size());

		ConstraintViolation<Network> violation = violations.iterator().next();
		assertEquals("IIII", violation.getInvalidValue());
		assertEquals(violation.getMessage(), "102, network code doesn't match [A-Za-z0-9\\*\\?]{1,2}");
	}

	@Test
	public void networkCodeWrong() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_charNETCODE.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);

		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(1, violations.size());

		ConstraintViolation<Network> violation = violations.iterator().next();
		assertEquals("#$", violation.getInvalidValue());
		assertEquals("102, network code doesn't match [A-Za-z0-9\\*\\?]{1,2}", violation.getMessage());
	}

	@Test
	public void networkInvalidStartTime() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_badNetSTARTTIME.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);

		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(1, violations.size());

		ConstraintViolation<Network> violation = violations.iterator().next();
		assertEquals("105, starttime should be before endtime", violation.getMessage());
	}

	@Test
	public void nullStationCode() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_nullSTACODE.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);
		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(0, violations.size());
		List<Station> stations = n.getStations();
		assertEquals(1, stations.size());
		Station station = stations.get(0);
		Set<ConstraintViolation<Station>> stationViolations = validator.validate(station);
		ConstraintViolation<Station> v = stationViolations.iterator().next();
		assertEquals(1, stationViolations.size());
		ConstraintViolation<Station> violation = stationViolations.iterator().next();
		assertEquals("202, station code doesn't match [A-Za-z0-9\\*\\?]{1,5}", violation.getMessage());

		station.setCode("ANMO");
		stationViolations = validator.validate(station);
		assertEquals(0, stationViolations.size());
	}

	@Test
	public void nullChannelCode() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_nullCHACODE.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);
		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(0, violations.size());
		List<Station> stations = n.getStations();
		assertEquals(1, stations.size());
		Station station = stations.get(0);
		Set<ConstraintViolation<Station>> stationViolations = validator.validate(station);
		assertEquals(0, stationViolations.size());
		List<Channel> channels = stations.get(0).getChannels();

		for (Channel channel : channels) {
			Set<ConstraintViolation<Channel>> channelViolations = validator.validate(channel);
			assertEquals(1, channelViolations.size());

			Iterator<ConstraintViolation<Channel>> it = channelViolations.iterator();
			assertTrue(it.hasNext());
			ConstraintViolation<Channel> violation = it.next();
			assertEquals("302, channel code doesn't match [A-Za-z0-9\\*\\?]{1,3}", violation.getMessage());
			channel.setCode("BHZ");
			channelViolations = validator.validate(channel);
			assertEquals(0, channelViolations.size());
		}
	}

	@Test
	public void nulllocationCode() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_nullLOCCODE.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);
		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(0, violations.size());
		List<Station> stations = n.getStations();
		assertEquals(1, stations.size());
		Station station = stations.get(0);
		Set<ConstraintViolation<Station>> stationViolations = validator.validate(station);
		assertEquals(0, stationViolations.size());
		List<Channel> channels = stations.get(0).getChannels();
		assertEquals(3, channels.size());
		for (Channel channel : channels) {
			Set<ConstraintViolation<Channel>> channelViolations = validator.validate(channel);
			assertEquals(0, channelViolations.size());

		}
	}

	@Test
	public void channelTooFarFromStation251() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_CHAtooFarFromSTA.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		in.close();
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);
		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(0, violations.size());
		List<Station> stations = n.getStations();
		assertEquals(1, stations.size());
		Station station = stations.get(0);
		Set<ConstraintViolation<Station>> stationViolations = validator.validate(station);
		assertEquals(1, stationViolations.size());
		ConstraintViolation<Station> violation = stationViolations.iterator().next();
		assertEquals(messages.get("station.channel.distance"), violation.getMessage());
	}

	@Test
	public void stageSequence() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("AUMCQBHZ_stageNOSEQUENCE.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);
		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(0, violations.size());
		List<Station> stations = n.getStations();
		assertEquals(1, stations.size());
		Station station = stations.get(0);
		Set<ConstraintViolation<Station>> stationViolations = validator.validate(station);
		assertEquals(0, stationViolations.size());

		assertNotNull(station.getChannels());
		assertFalse(station.getChannels().isEmpty());
		assertEquals(1, station.getChannels().size());
		Channel channel = station.getChannels().get(0);

		Set<ConstraintViolation<Channel>> channelViolations = validator.validate(channel);
		assertEquals(0, channelViolations.size());

		assertNotNull(channel.getResponse());
		assertNotNull(channel.getResponse().getStage());
		assertEquals(5, channel.getResponse().getStage().size());

		Set<ConstraintViolation<Response>> responseViolations = validator.validate(channel.getResponse());
		assertEquals(2, responseViolations.size());
		ConstraintViolation<Response> violation1 = responseViolations.iterator().next();
		ConstraintViolation<Response> violation2 = responseViolations.iterator().next();

		assertTrue("401, Stage number attribute must start at 1, be present in numerical order and have no gaps"
				.equals(violation1.getMessage())
				|| "402, The element <InputUnits> of a stage must match the element <OutputUnits> of the preceding stage, except for stages 0 or 1"
						.equals(violation1.getMessage()));

		assertTrue("401, Stage number attribute must start at 1, be present in numerical order and have no gaps"
				.equals(violation2.getMessage())
				|| "402, The element <InputUnits> of a stage must match the element <OutputUnits> of the preceding stage, except for stages 0 or 1"
						.equals(violation2.getMessage()));
	}

	@Test
	public void shouldPass402() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("402_pass.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);
		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(0, violations.size());
		List<Station> stations = n.getStations();
		assertEquals(1, stations.size());
		Station station = stations.get(0);
		Set<ConstraintViolation<Station>> stationViolations = validator.validate(station);
		assertEquals(0, stationViolations.size());

		assertNotNull(station.getChannels());
		assertFalse(station.getChannels().isEmpty());
		assertEquals(1, station.getChannels().size());
		Channel channel = station.getChannels().get(0);

		Set<ConstraintViolation<Channel>> channelViolations = validator.validate(channel, ResponseGroup.class);
		assertEquals(0, channelViolations.size());

		assertNotNull(channel.getResponse());
		assertNotNull(channel.getResponse().getStage());
		assertEquals(6, channel.getResponse().getStage().size());
		ResponseStage stage = channel.getResponse().getStage().get(2);
		Set<ConstraintViolation<Response>> responseViolations = validator.validate(channel.getResponse());
		assertEquals(0, responseViolations.size());

	}

	@Test
	public void nonZeroSampleRate() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_INSTSENS_notlisted.xml");
		FDSNStationXML root = (FDSNStationXML) jaxbUnmarshaller.unmarshal(in);

		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);
		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(0, violations.size());
		List<Station> stations = n.getStations();
		assertEquals(1, stations.size());
		Station station = stations.get(0);
		Set<ConstraintViolation<Station>> stationViolations = validator.validate(station);
		assertEquals(0, stationViolations.size());

		assertNotNull(station.getChannels());
		assertFalse(station.getChannels().isEmpty());
		assertEquals(1, station.getChannels().size());
		Channel channel = station.getChannels().get(0);

		Set<ConstraintViolation<Channel>> channelViolations = validator.validate(channel, ResponseGroup.class);
		assertEquals(1, channelViolations.size());
		ConstraintViolation<Channel> violation = channelViolations.iterator().next();
		assertEquals(messages.get("response.samplerate.407"), violation.getMessage());

	}

	@Test
	public void invalidAzimuth() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();

		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_invalidCHAAZ.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);
		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(0, violations.size());
		List<Station> stations = n.getStations();
		assertEquals(1, stations.size());
		Station station = stations.get(0);
		Set<ConstraintViolation<Station>> stationViolations = validator.validate(station);
		assertEquals(0, stationViolations.size());

		assertNotNull(station.getChannels());
		assertFalse(station.getChannels().isEmpty());
		assertEquals(3, station.getChannels().size());
		Channel channel = station.getChannels().get(0);

		Set<ConstraintViolation<Channel>> channelViolations = validator.validate(channel);
		// System.out.println("??????"+channelViolations.iterator().next().getMessage());
		assertEquals(1, channelViolations.size());

	}

	@Test
	public void digitialTransferFunction() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_digitalTransferFxn_NoDecimation.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network n = networks.get(0);
		assertNotNull(n);
		Set<ConstraintViolation<Network>> violations = validator.validate(n);
		assertEquals(0, violations.size());
		List<Station> stations = n.getStations();
		assertEquals(1, stations.size());
		Station station = stations.get(0);
		Set<ConstraintViolation<Station>> stationViolations = validator.validate(station);
		assertEquals(0, stationViolations.size());

		assertNotNull(station.getChannels());
		assertFalse(station.getChannels().isEmpty());
		assertEquals(1, station.getChannels().size());
		Channel channel = station.getChannels().get(0);

		Set<ConstraintViolation<Channel>> channelViolations = validator.validate(channel);
		assertEquals(0, channelViolations.size());

		assertNotNull(channel.getResponse());
		List<ResponseStage> stages = channel.getResponse().getStage();
		assertEquals(6, stages.size());

		for (ResponseStage stage : stages) {
			Set<ConstraintViolation<ResponseStage>> stageViolations = validator.validate(stage);
			if (stage.getNumber().intValue() == 1) {
				assertEquals(0, stageViolations.size());
			} else if (stage.getNumber().intValue() == 2) {
				assertEquals(0, stageViolations.size());
			} else if (stage.getNumber().intValue() == 3) {
				assertEquals(1, stageViolations.size());
			} else if (stage.getNumber().intValue() == 4) {
				assertEquals(1, stageViolations.size());
			} else if (stage.getNumber().intValue() == 5) {
				assertEquals(1, stageViolations.size());
			} else if (stage.getNumber().intValue() == 6) {
				assertEquals(1, stageViolations.size());
			}
		}
	}

	@Test
	public void mismatchedUnits() throws Exception {
		JAXBContext jaxbContext = (JAXBContext) JAXBContext.newInstance(FDSNStationXML.class);
		Unmarshaller xmlProcessor = jaxbContext.createUnmarshaller();
		in = this.getClass().getClassLoader().getResourceAsStream("IIKDAK10VHZ_StageunitsNoMatch.xml");
		FDSNStationXML root = (FDSNStationXML) xmlProcessor.unmarshal(in);
		List<Network> networks = root.getNetwork();

		Network ii = networks.get(0);
		assertNotNull(ii);
		Set<ConstraintViolation<Network>> violations = validator.validate(ii);
		assertEquals(0, violations.size());
		List<Station> stations = ii.getStations();
		assertEquals(1, stations.size());
		Station kdak = stations.get(0);
		Set<ConstraintViolation<Station>> stationViolations = validator.validate(kdak);
		assertEquals(0, stationViolations.size());

		assertNotNull(kdak.getChannels());
		assertFalse(kdak.getChannels().isEmpty());
		assertEquals(1, kdak.getChannels().size());
		Channel channel = kdak.getChannels().get(0);

		Set<ConstraintViolation<Channel>> channelViolations = validator.validate(channel);
		assertEquals(0, channelViolations.size());

		Response response = channel.getResponse();
		assertNotNull(response);
		Set<ConstraintViolation<Response>> responseViolations = validator.validate(response);
		assertEquals(1, responseViolations.size());
		ConstraintViolation<Response> violation = responseViolations.iterator().next();
		assertNotNull(violation);

		assertEquals(
				"402, The element <InputUnits> of a stage must match the element <OutputUnits> of the preceding stage, except for stages 0 or 1",
				violation.getMessage());
	}
}
