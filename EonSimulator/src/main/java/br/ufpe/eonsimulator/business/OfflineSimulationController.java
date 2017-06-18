package br.ufpe.eonsimulator.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.LinksCostWrapper;
import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.eonsimulator.modulation.ModulationFormatBitRateWrapper;
import br.ufpe.eonsimulator.rsa.RSAWrapper;
import br.ufpe.simulator.list.FixedArrayList;
import br.ufpe.simulator.utils.CollectionsUtils;

/**
 * A simple simulation approach based on creating connection requests.
 */
public class OfflineSimulationController extends AbstractSimulationController
		implements IsSimulationController {

	// Private class for Simulation Route information
	private static Logger logger = Logger
			.getLogger(OfflineSimulationController.class);

	@Override
	public void run(Simulation simulation) {
		int numberOfIterations = simulation.getNumberOfIterations();
		List<LinksCostWrapper> linksCostWrappers = new FixedArrayList<LinksCostWrapper>(
				simulation.getSimulationResultSetSize());
		for (int i = 0; i < numberOfIterations; i++) {
			simulation.clearArrivalRate();
			simulation.getTopology().updateLinksCost(i,
					simulation.getLinkCostFunction(), simulation.getAlfa(),
					linksCostWrappers);
			do {
				clearSimulation(simulation, logger);
				for (int numberConnectionIndex = 0; numberConnectionIndex < simulation
						.getMaxNumberConnection(); numberConnectionIndex++) {
					simulation.clearElapsedConnections(); // Removes all the
															// connections with
															// elapsed time;
					// Defines the node pair, the bit rate and the death time of
					// the
					// connection

					List<Double> bitRates = simulation.getTrafficGenerator()
							.getBitRateGen().getBitRates();

					if (bitRates != null) {

						for (Double bitRate : bitRates) {

							Connection connection = simulation
									.getTrafficGenerator().createConnection(
											simulation, bitRate);

							simulation.getSimulationResults()
									.incrementNumberOfRequests();
							simulation.getSimulationResults()
									.incrementNumberOfRequest(
											connection.getRequestedBitRate());

							// Calculate the routes using the routing algorithm
							List<Route> routes = simulation
									.getIsRoutingAlgorithm().createRoutes(
											connection,
											simulation.getTopology(),
											simulation.getCostFunction());

							Iterator<ModulationFormatBitRateWrapper> modulationFormatWrappers = simulation
									.getRSAAlgorithm()
									.createModulationFormatIterator(simulation,
											connection);

							while (modulationFormatWrappers.hasNext()
									&& !routes.isEmpty()) {

								ModulationFormatBitRateWrapper formatBitRateWrapper = modulationFormatWrappers
										.next();

								connection
										.setNumberSlotRequired(formatBitRateWrapper
												.getModulationFormat()
												.createNumberOfRequiredSlots(
														simulation
																.getSimulationParameters()
																.getConnectionSlotWidth(),
														connection
																.getRequestedBitRate()));

								connection
										.setRequiredOSNR(formatBitRateWrapper
												.getModulationFormat()
												.createRequiredOSNR(
														simulation,
														connection
																.getRequestedBitRate()));

								List<RSAWrapper> rsaWrappers = simulation
										.getRSAAlgorithm().createRsaWrappers(
												routes,
												simulation,
												connection,
												formatBitRateWrapper
														.getModulationFormat(),
												false);
								routes.clear();
								List<Route> qotRoutes = new ArrayList<Route>();
								for (RSAWrapper rsaWrapper : rsaWrappers) {
									routes.add(rsaWrapper.getRoute());
									if (rsaWrapper.isOSNRValid()) {
										simulation.getTopology()
												.updateAllocatedSlots(
														rsaWrapper.getRoute(),
														rsaWrapper.getnSlots());
										simulation
												.getTopology()
												.updateAllocatedSlots(
														simulation
																.getTopology()
																.getDualRoute(
																		rsaWrapper
																				.getRoute()),
														rsaWrapper.getnSlots());
										qotRoutes.add(rsaWrapper.getRoute());
									}
								}

								routes = CollectionsUtils.diff(routes,
										qotRoutes);
							}
						}
					}
				}
			} while (simulation.nextSimulation());
		}
		simulation.getOutputLinkCostFunction().write(linksCostWrappers);
	}
}
