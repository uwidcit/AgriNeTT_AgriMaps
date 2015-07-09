from django.contrib.gis.utils import LayerMapping
from models import Contours

contoursJsonFile = '/Users/kiran/Downloads/contours.json'
mapping = {
	'trt':'TRTO0326_',
	'trt_i':'TRTO0326_',
	'height':'HEIGHT',
	'htm': 'HTM',
	'rpoly':'RPOLY_',
	'lpoly':'LPOLY_',
	'tnode':'TNODE_',
	'fnode':'FNODE_',
	'length':'LENGTH',
	'geomData':'MULTILINESTRING'
}

lm = LayerMapping(Contours,contoursJsonFile,mapping)
lm.save(verbose=True)