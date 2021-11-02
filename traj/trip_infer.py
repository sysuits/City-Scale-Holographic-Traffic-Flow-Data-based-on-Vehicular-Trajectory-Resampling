import pandas as pd

from traj import network
from traj.network import RdNet
from datetime import timedelta


class Intersection:
    insec_dict = dict()

    def __init__(self, id):
        self.id = id
        self.phases = dict()
        self.signalized = False
        Intersection.insec_dict[id] = self

    def next_node_phases(self, now, up_node, dn_node):
        rid = up_node.id + '_' + self.id
        l = RdNet.rd_len[rid]
        t_max = l / network.MIN_SPD
        t_max = max(now) + timedelta(seconds=t_max)
        t_min = l / network.MAX_SPD
        t_min = min(now) + timedelta(seconds=t_min)
        dir = rid + '_' + dn_node.id
        if self.signalized:
            phases = [x for x in self.phases[dir] if x.ft < t_max and x.tt > t_min]
        else:
            phases = [Phase(dir, t_min, t_max, False)]
        return phases


class Phase:
    gid = 0
    all = dict()

    def __init__(self, dir, ft, tt, real=True):
        self.id = Phase.gid
        self.dir = dir
        self.ft = ft
        self.tt = tt
        self.real = real
        Phase.gid += 1
        Phase.all[Phase.gid] = self


class PgNode:
    def __init__(self, dir, times):
        self.nodes = dir.split('_')
        self.f_node = self.nodes[0]
        self.t_node = self.nodes[1]
        if len(self.nodes) == 3:
            self.next_node = self.nodes[2]
        # else:
        #     self.next_node = '0'
        self.dir = dir
        self.times = times
        self.parents = []
        self.children = []

    def add_child(self, pg_node):
        self.children.append(pg_node)
        pg_node.parents.append(self)

    def add_children(self, pg_nodes):
        for pg in pg_nodes:
            self.add_child(pg)

    def accessible(self, tt):
        l = RdNet.rd_len['_'.join(self.nodes[1:])]
        t_max = l / network.MIN_SPD
        t_max = max(self.times) + timedelta(seconds=t_max)
        t_min = l / network.MAX_SPD
        t_min = min(self.times) + timedelta(seconds=t_min)
        return t_min <= tt < t_max


class PassNode:
    def __init__(self, nid, dir, t_pass):
        self.nid = nid
        self.dir = dir
        self.t_pass = t_pass


def read_signal(sig_file):
    data = pd.read_csv(sig_file)
    data['ftime'] = pd.to_datetime(data['ftime'])
    data['ttime'] = pd.to_datetime(data['ttime'])
    for idx, row in data.iterrows():
        nid = row['nid']
        if nid not in Intersection.insec_dict:
            node = Intersection(nid)
        else:
            node = Intersection.insec_dict[nid]
        node.signalized = True
        dir = row['dir']
        if dir not in node.phases:
            node.phases[dir] = []
        node.phases[dir].append(Phase(dir, row['ftime'], row['ttime']))
    return True


def createPgNodes(phases, existed=None):
    if existed is None:
        pgs = [PgNode(x.dir, [x.ft, x.tt]) for x in phases]
    else:
        pgs = [get_pg(x, existed) for x in phases]
    return pgs


def get_pg(phase, existed):
    key = str(phase.ft) + '#' + str(phase.tt)
    if existed[key] is None:
        existed[key] = PgNode(phase.dir, [phase.ft, phase.tt])
    return existed[key]


def next_node_phases(nid, now, up_node, dn_node):
    intersection = Intersection.insec_dict[nid]
    return intersection.next_node_phases(now, up_node, dn_node)


def checkPath(path_nodes, ft, tt):
    res = dict()
    pass_nodes = dict()

    n0 = path_nodes[1]
    d0 = '_'.join(path_nodes[0:3])
    pass_nodes[n0] = PassNode(n0, d0, ft)
    nt = path_nodes[-1]
    dt = '_'.join(path_nodes[-2:])
    pass_nodes[nt] = PassNode(nt, dt, tt)
    pg_root = PgNode(d0, [ft])
    pg_end = PgNode(dt, [tt])
    pg_current = [pg_root]
    pg_layer = dict()
    pg_layer[n0] = pg_current
    pg_layer[nt] = [pg_end]

    for i in range(1, len(path_nodes) - 2):
        nid = path_nodes[i + 1]
        dir = '_'.join(path_nodes[i:i + 2])
        children = dict()
        for pg in pg_current:
            phases = next_node_phases(nid, pg.times, path_nodes[i], path_nodes[i + 2])
            pg_nodes = createPgNodes(phases, existed=children)
            pg.add_children(pg_nodes)
        pg_layer[nid] = children
        pg_current = children

    # valid = [x for x in pg_current if x.tims[0] <= tt < x.times[1]]
    valid = [x for x in pg_current if x.accessible(tt)]
    if not valid:
        res['accessible'] = False
        return res
    for i in range(len(path_nodes) - 2, 1, -1):
        nid = path_nodes[i]
        pg_layer[nid] = valid
        upper_valid = []
        for v_node in valid:
            upper_valid += [parent for parent in v_node.parents if parent not in upper_valid]
        if not upper_valid:
            res['accessible'] = False
            return res
        valid = upper_valid

    res['accessible'] = True
    res['pass_graph'] = pg_layer
    res['pass_nodes'] = pass_nodes
    return res


def path(rd_net, source, target, cutoff=10, maxRoute=10):
    selected_paths = rd_net.all_link_path(source, target, cutoff=cutoff, maxRoute=maxRoute)
    return [link_to_node(path) for path in selected_paths]


def link_to_node(links):
    f = '_'.join([x.split('_')[0] for x in links[:-1]])
    return f + '_' + links[-1]
