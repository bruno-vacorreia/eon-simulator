package br.ufpe.eonsimulator.rsa;

import java.util.Iterator;
import java.util.List;

import br.ufpe.eonsimulator.domain.Connection;
import br.ufpe.eonsimulator.domain.Route;
import br.ufpe.eonsimulator.domain.Simulation;
import br.ufpe.eonsimulator.modulation.IsModulationFormat;
import br.ufpe.eonsimulator.modulation.ModulationFormatBitRateWrapper;

public interface IsRSAAlgorithm {

	RSAWrapper getRSAWrapper(List<Route> routes, Simulation simulation,
			Connection connection);

	Iterator<ModulationFormatBitRateWrapper> createModulationFormatIterator(
			Simulation simulation, Connection connection);

	List<RSAWrapper> createRsaWrappers(List<Route> routes,
			Simulation simulation, Connection connection,
			IsModulationFormat modulationFormat, boolean doSpectrumAssignment);

}
