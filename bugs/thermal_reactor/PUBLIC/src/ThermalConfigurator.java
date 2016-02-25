
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class ThermalConfigurator {

	public static void main(String[] args) throws IOException {
            ThermalConfigurator configurator = new ThermalConfigurator();
            String output = configurator.configure(System.in);
            System.out.print(output);
	}

        public String configure(InputStream source) throws IOException {
            ThermalConfigurationParser parser = new ThermalConfigurationParser(source);
            ThermalConfiguration configuration = parser.parseConfiguration();
            return configuration.renderReport();
        }

    class ThermalConfigurationParser {
        InputStream source;

        public ThermalConfigurationParser(InputStream source) {
            this.source = source;
        }

        public ThermalConfiguration parseConfiguration() throws IOException {
            List<ThermalProbe> probes = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(source));
            // Read prob line
            int i = reader.read();
            while(i > -1) {
                Character c = (char)i;
                if(c == ' ') {
                    i = reader.read();
                    continue;
                } else if(c == '\n') {
                    break;
                }
                String name = c.toString();
                ThermalProbe probe = new ThermalProbe(name);
                probes.add(probe);
                i = reader.read();
            }
            // Read levels
            while(i > -1) {
                i = reader.read();
                Character c = (char)i;
                if(i < 0 || c == '\n') {
                    break;
                }
                int levelIndex = 0;
                int probeIndex = Integer.parseInt(c.toString());

                while(i > -1) {
                    probeIndex += 1;
                    i = reader.read();
                    c = (char)i;
                    if(i < 0 || c == '\n') {
                        break;
                    }
                    i = reader.read();
                    c = (char)i;
                    if(i < 0 || c == '\n') {
                        break;
                    } else if(c == 'x') {
                        ThermalProbe probe = probes.get(probeIndex);
                        probe.level = levelIndex;
                    }
                }
            }
            ThermalConfiguration configuration = new ThermalConfiguration();
            for(ThermalProbe probe : probes) {
                if(probe.isDeployed()) {
                    configuration.addProbe(probe);
                }
            }
            if(configuration.isEmpty()) {
                System.err.println("Error: empty input");
                System.exit(1);
            }
            return configuration;
        }
    }

    class ThermalConfiguration {
        List<ThermalProbe> probes = new ArrayList<>();

        public void addProbe(ThermalProbe probe) {
            probes.add(probe);
        }

        public String renderReport() {
            Integer maxLevel = 0;
            StringBuilder res = new StringBuilder();
            res.append("Probes configuration after request:\n");
            res.append(" ");
            for(int i = 0; i < probes.size(); i++) {
                ThermalProbe probe = probes.get(i);
                res.append(" ");
                res.append(probe.name);
                maxLevel = probe.level;
            }
            res.append("\n");
            for(int i = 0; i < maxLevel; i++) {
                res.append(i);
                for(int j = 0; j < probes.size(); j++) {
                    ThermalProbe probe = probes.get(i);
                    if(probe.level > j) {
                        res.append(" |");
                    } else if(probe.level == j) {
                        res.append(" *");
                    } else {
                        res.append("  ");
                    }
                }
                res.append("\n");
            }
            return res.toString();
        }

        public Boolean isEmpty() {
            return true;
        }
    }

    class ThermalProbe {
        String name;

        Integer level = null;

        public ThermalProbe(String name) {
            this.name = name;
        }

        public Boolean isDeployed() {
            return level > 0;
        }
    }
}