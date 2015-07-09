from django.contrib.gis.utils import LayerMapping
from models import soilFeatures

domJsonFile = '/Users/kiran/Downloads/soilDom.xls'
mapping = {
    'domsoil' : 'DOMSOIL',
    'sand_prop' : 'Sand - proportionalised',
    'silt_prop' : 'Silt - proportionalised',
    'clay_prop' : 'Clay - proportionalised',
    'horizon_to_dept' : 'Horizon 1 to Depth of (centimetres)',
    'phhorizon1' : 'PHHORIZON1',
    'ca' : 'CA',
    'mg' : 'MG',
    'k' : 'K',
    'na' : 'NA',
    'cec' : 'C.E.C.',
    'exch_cations' : 'Exchangeable Cations',
    'c' : 'C',
    'n' : 'N',
    'freecaco3' : 'FREECACO3',
    'fertility' : 'FERTILITY'
}


lm = LayerMapping(soilFeatures,domJsonFile,mapping)
lm.save(verbose=True)