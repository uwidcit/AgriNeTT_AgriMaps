# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import django.contrib.gis.db.models.fields


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0013_auto_20150211_1434'),
    ]

    operations = [
        migrations.CreateModel(
            name='landUse',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('area', models.FloatField()),
                ('perimeter', models.FloatField()),
                ('landuse_field', models.FloatField()),
                ('landuse_id', models.FloatField()),
                ('classifaca', models.CharField(max_length=100)),
                ('symbols_field', models.CharField(max_length=30)),
                ('geomData', django.contrib.gis.db.models.fields.MultiPolygonField(srid=4326)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
