from django.contrib.gis.utils import LayerMapping
from models import landUse

landUseJsonFile = '/Users/kiran/Downloads/landUse/landuse.shp'
mapping = {
	'area' : 'AREA',
    'perimeter' : 'PERIMETER',
    'landuse_field' : 'LANDUSE_',
    'landuse_id' : 'LANDUSE_ID',
    'classifaca' : 'CLASSIFACA',
    'symbols_field' : 'SYMBOLS_',
    'geomData' : 'MULTIPOLYGON'
}

lm = LayerMapping(landUse,landUseJsonFile,mapping)
lm.save(verbose=True)