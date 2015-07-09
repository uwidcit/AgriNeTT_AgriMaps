from django.contrib.gis.utils import LayerMapping
from models import Roads

roadsJsonFile = '/Users/kiran/Downloads/roads.json'
mapping = {
	'objectID':'OBJECTID',
	'fnode':'FNODE_',
	'tnode':'TNODE_',
	'length':'LENGTH',
	'streetgis':'STREETGIS',
	'source':'SOURCE',
	'geomData':'MULTILINESTRING'
}

lm = LayerMapping(Roads,roadsJsonFile,mapping)
lm.save(verbose=True)