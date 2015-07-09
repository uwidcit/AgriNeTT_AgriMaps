# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import django.contrib.gis.db.models.fields


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0006_auto_20150128_1702'),
    ]

    operations = [
        migrations.AlterField(
            model_name='pipelines',
            name='geomData',
            field=django.contrib.gis.db.models.fields.GeometryField(srid=4326),
            preserve_default=True,
        ),
    ]
