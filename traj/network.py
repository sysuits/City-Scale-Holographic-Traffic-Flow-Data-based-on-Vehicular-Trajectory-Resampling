import networkx as nx
import pandas as pd

MAX_SPD = 80.0 / 3.6
MIN_SPD = 5.0 / 3.6


class RdNet:
    rd_len = dict()
    rd_data = pd.DataFrame()
    node_graph = nx.DiGraph()
    link_graph = nx.DiGraph()

    def __init__(self, net_file):
        RdNet.rd_data = pd.read_csv(net_file)
        RdNet.rd_len = dict(zip(RdNet.rd_data['cid'], RdNet.rd_data['len']))
        RdNet.rd_data['fn'] = RdNet.rd_data.apply(lambda x: x['cid'].split('_')[0], axis=1)
        RdNet.rd_data['tn'] = RdNet.rd_data.apply(lambda x: x['cid'].split('_')[1], axis=1)
        RdNet.rd_data.apply(lambda x: RdNet.node_graph.add_edge(x['fn'], x['tn'], len=x['len']), axis=1)
        for idx, row in RdNet.rd_data.iterrows():
            if pd.notna(row['dnroad']):
                for dn in row['dnroad'].split('#'):
                    RdNet.link_graph.add_edge(row['cid'], dn, len=RdNet.rd_len[row['cid']] + RdNet.rd_len[dn])

    def all_simple_path(self, graph, source, target, cutoff):
        paths = list(nx.all_simple_paths(graph, source, target, cutoff=cutoff))
        if paths:
            paths.sort(key=lambda x: self.path_len(x))
        return paths

    def path_len(self, path):
        return sum([RdNet.rd_len[seg] for seg in path])

    def all_link_path(self, source, target, cutoff, maxRoute):
        all_path = self.all_simple_path(self.link_graph, source, target, cutoff=cutoff)
        return all_path[:maxRoute]

    def all_node_path(self, source, target, cutoff=10, maxRoute=10):
        all_path = self.all_simple_path(self.node_graph, source, target, cutoff=cutoff)
        return all_path[:maxRoute]
